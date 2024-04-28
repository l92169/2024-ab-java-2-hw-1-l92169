package com.example.service;

import com.example.config.MinioProperties;
import com.example.dto.ImageDto;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {
  private final MinioClient client;
  private final MinioProperties minioProperties;
  private String bucketName;

  @PostConstruct
  public void init() throws Exception {
    bucketName = "minio-storage";
    boolean bucketExists = client.bucketExists(BucketExistsArgs.builder()
        .bucket(bucketName).build());
    if (!bucketExists) {
      client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
    }
  }


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

  public long getSize(String uuid) throws Exception {
    return client.statObject(
        StatObjectArgs.builder()
            .bucket(bucketName)
            .object(uuid)
            .build()
    ).size();

  }
}
