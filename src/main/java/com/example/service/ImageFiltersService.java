package com.example.service;

import com.example.domain.ImageT;
import com.example.domain.Request;
import com.example.domain.User;
import com.example.domain.enums.FILTER;
import com.example.domain.enums.STATUS;
import com.example.dto.response.ApplyImageFiltersResponse;
import com.example.dto.response.GetModifiedImageByRequestIdResponse;
import com.example.exceptions.ImageNotFoundException;
import com.example.kafka.message.ImageWip;
import com.example.repository.ImageRepository;
import com.example.repository.RequestRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageFiltersService {
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final ImageRepository imageRepository;
  private final RequestRepository requestRepository;
  private final UserService userService;
  private final MinioService minioService;

  @Transactional
  public ApplyImageFiltersResponse applyImageFilters(UUID imageId, List<FILTER> filters) {
    User currUser = userService.getCurrentUser();
    Optional<ImageT> image = imageRepository.findImageById(imageId);
    if (image.isEmpty() || !image.get().getUserId().equals(currUser.getId())) {
      throw new ImageNotFoundException("Файл не найден в системе или недоступен");
    }
    Request request = requestRepository.save(
        new Request().status(STATUS.WIP).originImageId(imageId).changedImageId(null));
    ImageWip message = new ImageWip(UUID.fromString(image.get().getLink()), request.id(),
        filters.stream().map(Enum::name).toList());
    kafkaTemplate.send("images.wip", message);
    return new ApplyImageFiltersResponse(request.id());
  }

  @Transactional
  public void setDone(UUID imageId, UUID requestId) throws Exception {
    Request request = requestRepository.findRequestById(requestId).orElseThrow();
    ImageT originalImage = imageRepository.findImageById(request.originImageId()).orElseThrow(
        () -> new ImageNotFoundException("Файл не найден в системе или недоступен"));
    String editedFilename = "%s_edited.%s".formatted(FilenameUtils.getName(originalImage.getName()),
        FilenameUtils.getExtension(originalImage.getName()));
    long size = minioService.getSize(String.valueOf(imageId));
    ImageT editedImage = new ImageT(UUID.randomUUID(), editedFilename, size,
        String.valueOf(imageId), originalImage.getUserId());
    request.status(STATUS.DONE).changedImageId(editedImage.getId());
    imageRepository.save(editedImage);
    requestRepository.save(request);
  }

  public GetModifiedImageByRequestIdResponse getModifiedImageByRequestId(UUID imageId,
                                                                         UUID requestId) {
    User currUser = userService.getCurrentUser();
    Optional<ImageT> image = imageRepository.findImageById(imageId);
    if (image.isEmpty() || !image.get().getUserId().equals(currUser.getId())) {
      throw new ImageNotFoundException("Файл не найден в системе или недоступен");
    }
    Request request = requestRepository.findRequestById(requestId).orElseThrow();
    if (request.status().equals(STATUS.DONE)) {
      return new GetModifiedImageByRequestIdResponse(request.changedImageId(), STATUS.DONE);
    }
    return new GetModifiedImageByRequestIdResponse(request.originImageId(), STATUS.WIP);
  }
}
