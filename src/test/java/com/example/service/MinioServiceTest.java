package com.example.service;

import com.example.config.MinioProperties;
import com.example.dto.ImageDto;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@DisplayName("Minio Service Test")
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    private MinioProperties minioProperties = new MinioProperties("test-url", 0,  "test-access-key", "test-secret-key", true,"test-bucket", 0);

    private MinioService minioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        minioService = new MinioService(minioClient, minioProperties);
    }

    @Test
    @DisplayName("Test uploadImage method")
    void uploadImage() throws Exception {
        // Given
        MultipartFile multipartFile = new MockMultipartFile("test-image.jpg", "test-image.jpg", "image/jpeg", "test".getBytes());

        String fileId = UUID.randomUUID().toString();
        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        // When
        ImageDto result = minioService.uploadImage(multipartFile);

        // Then
        assertNotNull(result);
        assertEquals("test-image.jpg", result.getName());
        assertEquals(4, result.getSize());
        assertNotNull(result.getLink());
    }


}
