package com.example.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
  private String url;
  private int port;
  private String accessKey;
  private String secretKey;
  private boolean secure;
  private String bucket;
  private long imageSize;
  private int ttl;
}
