package com.example.service;

import com.example.domain.ImageT;
import com.example.domain.User;
import com.example.dto.response.GetImagesResponse;
import com.example.dto.response.Image;
import com.example.dto.response.UiSuccessContainer;
import com.example.exceptions.ImageNotFoundException;
import com.example.mapper.ImagesMapper;
import com.example.repository.ImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private ImagesMapper mapper;

    @InjectMocks
    private ImageService imageService;
    @Test
    void getImages_ReturnsGetImagesResponse() {
        // Given
        User currentUser = new User();
        when(userService.getCurrentUser()).thenReturn(currentUser);

        List<ImageT> imageTList = Arrays.asList(new ImageT(), new ImageT());
        when(repository.findAllByUserId(currentUser.getId())).thenReturn(imageTList);

        List<Image> expectedImages = Arrays.asList(new Image(), new Image());
        when(mapper.imagesToImagesDto(imageTList)).thenReturn(expectedImages);

        // When
        Object result = imageService.getImages();

        // Then
        assertEquals(GetImagesResponse.class, result.getClass());
        GetImagesResponse response = (GetImagesResponse) result;
        assertEquals(expectedImages, response.getImages());

        // Verify interactions
        verify(userService).getCurrentUser();
        verify(repository).findAllByUserId(currentUser.getId());
        verify(mapper).imagesToImagesDto(imageTList);
    }

    @Test
    @DisplayName("Test existsAll method")
    void testExistsAll() {
        // Given
        List<UUID> imageIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        when(repository.existsImagesByIdIn(imageIds)).thenReturn(true);

        // When
        boolean result = imageService.existsAll(imageIds);

        // Then
        assertTrue(result);
        verify(repository, times(1)).existsImagesByIdIn(imageIds);
    }



    @Test
    void deleteImage_ImageNotFound_ThrowsImageNotFoundException() {
        // Given
        UUID imageId = UUID.randomUUID();
        when(repository.findImageById(imageId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ImageNotFoundException.class, () -> imageService.deleteImage(imageId));

        // Verify interactions
        verify(repository).findImageById(imageId);
        verify(repository, never()).deleteById(any());
        verify(repository, never()).existsImageById(any());
    }

    @Test
    void deleteImage_ImageOwnedByDifferentUser_ThrowsImageNotFoundException() {
        // Given
        UUID imageId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(0L); // Different ID
        when(userService.getCurrentUser()).thenReturn(currentUser);

        ImageT imageT = new ImageT();
        imageT.setUserId(1L); // Different user ID
        when(repository.findImageById(imageId)).thenReturn(Optional.of(imageT));

        // When/Then
        assertThrows(ImageNotFoundException.class, () -> imageService.deleteImage(imageId));

        // Verify interactions
        verify(userService).getCurrentUser();
        verify(repository).findImageById(imageId);
        verify(repository, never()).deleteById(any());
        verify(repository, never()).existsImageById(any());
    }

    @Test
    void deleteImage_ImageExistsAndBelongsToCurrentUser_SuccessfullyDeleted() {
        // Arrange
        UUID imageId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(0l);
        ImageT image = new ImageT();
        image.setUserId(currentUser.getId());
        Optional<ImageT> optionalImage = Optional.of(image);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(repository.findImageById(imageId)).thenReturn(optionalImage);
        when(repository.existsImageById(imageId)).thenReturn(false); // Simulate image does not exist after deletion

        // Act
        UiSuccessContainer result = imageService.deleteImage(imageId);

        // Assert
        assertTrue(result.getSuccess());

        // Verify that repository.deleteById() was called with the correct imageId
        verify(repository).deleteById(imageId);
        // Verify that repository.existsImageById() was called with the correct imageId
        verify(repository).existsImageById(imageId);
    }

    @Test
    void deleteImage_ImageExistsButDoesNotBelongToCurrentUser_ThrowsImageNotFoundException() {
        // Arrange
        UUID imageId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(0l);
        ImageT image = new ImageT();
        image.setUserId(1l); // Image does not belong to current user
        Optional<ImageT> optionalImage = Optional.of(image);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(repository.findImageById(imageId)).thenReturn(optionalImage);

        // Act and Assert
        assertThrows(ImageNotFoundException.class, () -> imageService.deleteImage(imageId));

        // Verify that repository.deleteById() was not called
        verify(repository, never()).deleteById(imageId);
        // Verify that repository.existsImageById() was not called
        verify(repository, never()).existsImageById(imageId);
    }

    @Test
    void deleteImage_ImageExistsAndBelongsToCurrentUser_SuccessfullyDeleted_ImageStillExists() {
        // Arrange
        UUID imageId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(0l);
        ImageT image = new ImageT();
        image.setUserId(currentUser.getId());
        Optional<ImageT> optionalImage = Optional.of(image);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(repository.findImageById(imageId)).thenReturn(optionalImage);
        when(repository.existsImageById(imageId)).thenReturn(true); // Simulate image still exists after deletion

        // Act
        UiSuccessContainer result = imageService.deleteImage(imageId);

        // Assert
        assertFalse(result.getSuccess());

        // Verify that repository.deleteById() was called with the correct imageId
        verify(repository).deleteById(imageId);
        // Verify that repository.existsImageById() was called with the correct imageId
        verify(repository).existsImageById(imageId);
    }
}
