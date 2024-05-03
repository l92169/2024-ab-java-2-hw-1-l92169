package com.example.service;

import com.example.domain.ImageT;
import com.example.domain.User;
import com.example.dto.ImageDto;
import com.example.dto.response.GetImagesResponse;
import com.example.dto.response.Image;
import com.example.dto.response.UiSuccessContainer;
import com.example.dto.response.UploadImageResponse;
import com.example.exceptions.ImageNotFoundException;
import com.example.mapper.ImagesMapper;
import com.example.repository.ImageRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageRepository repository;
  private final UserService userService;
  private final MinioService service;
  private final ImagesMapper mapper;

  public boolean existsAll(List<UUID> imageIds) {
    return repository.existsImagesByIdIn(imageIds);
  }

  public byte[] downloadImage(UUID imageId) throws Exception {
    User currUser = userService.getCurrentUser();
    Optional<ImageT> image = repository.findImageById(imageId);
    if (image.isEmpty() || !image.get().getUserId().equals(currUser.getId())) {
      throw new ImageNotFoundException("Файл не найден в системе или недоступен");
    }
    return service.downloadImage(image.get().getLink());
  }

  public UploadImageResponse uploadImage(MultipartFile file) throws Exception {
    ImageDto imageDto = service.uploadImage(file);
    ImageT imageT = new ImageT().setName(imageDto.getName()).setSize(imageDto.getSize())
        .setLink(imageDto.getLink()).setUserId(userService.getCurrentUser().getId())
        .setId(UUID.randomUUID());
    imageT = repository.save(imageT);
    return new UploadImageResponse(imageT.getId());
  }

  public UiSuccessContainer deleteImage(UUID imageId) {
    User currUser = userService.getCurrentUser();
    Optional<ImageT> image = repository.findImageById(imageId);
    if (image.isEmpty() || !image.get().getUserId().equals(currUser.getId())) {
      throw new ImageNotFoundException("Файл не найден в системе или недоступен");
    }
    repository.deleteById(imageId);
    if (!repository.existsImageById(imageId)) {
      return new UiSuccessContainer(true);
    }
    return new UiSuccessContainer(false);
  }

  public Object getImages() {
    User currUser = userService.getCurrentUser();
    List<ImageT> imagesT = repository.findAllByUserId(currUser.getId());
    List<Image> images = mapper.imagesToImagesDto(imagesT);
    return new GetImagesResponse(images);
  }
}