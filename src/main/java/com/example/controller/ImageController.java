package com.example.controller;

import com.example.dto.UiSuccessContainer;
import com.example.dto.UploadImageResponse;
import com.example.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Image Controller", description = "Базовый CRUD API для работы с картинками")
public class ImageController {

    private final ImageService service;

    @Operation(summary = "Загрузка нового изображения в систему")
    @PostMapping(value = "/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успех выполнения операции",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UploadImageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Файл не прошел валидацию",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UiSuccessContainer.class))}),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UiSuccessContainer.class))})})
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.getSize() > 10 * 1024 * 1024 || !file.getOriginalFilename().matches(".+\\.(png|jpeg)$"))
            return ResponseEntity.status(400).body(new UiSuccessContainer(false, "Файл не прошел валидацию"));
        return ResponseEntity.status(200).body(service.uploadImage(file));
    }

    @Operation(summary = "Скачивание файла по ИД")
    @GetMapping(value = "/image/{image-id}", produces = MediaType.IMAGE_PNG_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успех выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Файл не найден в системе или недоступен",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UiSuccessContainer.class))}),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UiSuccessContainer.class))})})
    public byte[] downloadImage(@PathVariable(required = true, name = "image-id") UUID imageId) throws Exception {
        return service.downloadImage(imageId);
    }

    @Operation(summary = "Удаление файла по ИД")
    @DeleteMapping("/image/{image-id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успех выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Файл не найден в системе или недоступен",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UiSuccessContainer.class))}),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UiSuccessContainer.class))})})
    public ResponseEntity<?> deleteImage(@PathVariable(required = true, name = "image-id") UUID imageId) {
        return ResponseEntity.ok().body(service.deleteImage(imageId));
    }

    @Operation(summary = "Получение списка изображений, которые доступны пользователю")
    @GetMapping("/images")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успех выполнения операции"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UiSuccessContainer.class))})})
    public ResponseEntity<?> getImages() {
        return ResponseEntity.ok().body(service.getImages());
    }
}