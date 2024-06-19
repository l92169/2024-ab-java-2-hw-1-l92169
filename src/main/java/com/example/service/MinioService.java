package com.example.service;

import com.example.config.MinioProperties;
import com.example.dto.ImageDto;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketLifecycleArgs;
import io.minio.StatObjectArgs;
import io.minio.messages.Expiration;
import io.minio.messages.LifecycleConfiguration;
import io.minio.messages.LifecycleRule;
import io.minio.messages.RuleFilter;
import io.minio.messages.Status;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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

  public String uploadImage(
      final byte[] file,
      final String mediaType,
      final String prefix
  ) throws Exception {
    String fileId = prefix + UUID.randomUUID();

    InputStream inputStream = new ByteArrayInputStream(file);
    client.putObject(
        PutObjectArgs.builder()
            .bucket(minioProperties.getBucket())
            .object(fileId)
            .stream(inputStream, file.length, -1)
            .contentType(mediaType)
            .build()
    );

    List<LifecycleRule> rules = new ArrayList<>();
    rules.add(
        new LifecycleRule(
            Status.ENABLED,
            null,
            new Expiration((ZonedDateTime) null, minioProperties.getTtl(), null),
            new RuleFilter("expiry/"),
            "rule1",
            null,
            null,
            null));
    LifecycleConfiguration config = new LifecycleConfiguration(rules);
    client.setBucketLifecycle(SetBucketLifecycleArgs
        .builder()
        .bucket(minioProperties.getBucket())
        .config(config)
        .build());
    return fileId;
  }


  /**
   * Upload image.
   *
   * @param file
   * @param mediaType
   * @return image id.
   * @throws Exception
   */
  public String uploadImage(
      final byte[] file,
      final String mediaType
  ) throws Exception {
    return uploadImage(file, mediaType, "");
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
