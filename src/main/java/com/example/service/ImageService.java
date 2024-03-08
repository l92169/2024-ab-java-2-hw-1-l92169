package com.example.service;

import com.example.domain.Image;
import com.example.domain.Operation;
import com.example.dto.ImageDto;
import com.example.dto.OperationDto;
import com.example.exceptions.ImageNotFoundException;
import com.example.mapper.ImagesMapper;
import com.example.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository repository;
    private final ImagesMapper mapper;
    private final OperationService operationService;
    private final MinioService service;

    public boolean existsAll(List<Integer> imageIds) {
        return repository.existsImagesByIdIn(imageIds);
    }

    public List<Image> getAllImages(List<Integer> imageIds) {
        return repository.findAllByIdIn(imageIds);
    }

    @Cacheable(value = "ImageService::getImageMeta", key = "#id", condition="#id!=null")
    public ImageDto getImageMeta(int id) {
        Optional<Image> imageOptional = repository.findImageById(id);
        if (imageOptional.isEmpty()) {
            throw new ImageNotFoundException(id+"");
        }

        ImageDto image = mapper.imageToImageDto(imageOptional.get());

        operationService.logOperation(
                new OperationDto(
                        String.format("Read image metadata: %s", image),
                        LocalDateTime.now(),
                        Operation.OperationType.WRITE
                )
        );

        return image;
    }

    public byte[] downloadImage(String link) throws Exception {
        if (!repository.existsImagesByLink(link)) {
            throw new ImageNotFoundException(link);
        }
        return service.downloadImage(link);
    }

    @Cacheable(value = "ImageService::getImageMeta", key = "#file != null ? #file.originalFilename : null", condition="#file!=null")
    public ImageDto uploadImage(MultipartFile file) throws Exception {
        ImageDto image = service.uploadImage(file);
        repository.save(mapper.imageDtoToImage(image));

        operationService.logOperation(
                new OperationDto(
                        String.format("Upload image: %s", image),
                        LocalDateTime.now(),
                        Operation.OperationType.WRITE
                )
        );
        return image;
    }

}
