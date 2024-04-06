package com.example.service;

import com.example.config.MinioProperties;
import com.example.dto.ImageDto;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {

  private final MinioClient client;
  private final MinioProperties minioProperties;

  public ImageDto uploadImage(MultipartFile file) throws Exception {
    String fileId = UUID.randomUUID().toString();
    InputStream inputStream = new ByteArrayInputStream(file.getBytes());

    client.putObject(
        PutObjectArgs.builder()
            .bucket(minioProperties.getBucket())
            .object(fileId)
            .stream(inputStream, file.getSize(), minioProperties.getImageSize())
            .contentType(file.getContentType())
            .build()
    );

    return new ImageDto(file.getOriginalFilename(), file.getSize(), fileId);
  }

  public byte[] downloadImage(String fileId) throws Exception {
    return IOUtils.toByteArray(client.getObject(
        GetObjectArgs.builder()
            .bucket(minioProperties.getBucket())
            .object(fileId)
            .build()));
  }
}
