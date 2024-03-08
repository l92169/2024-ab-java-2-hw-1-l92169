package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String url;
    private int port;
    private String accessKey;
    private String secretKey;
    private boolean secure;
    private String bucket;
    private long imageSize;
}
