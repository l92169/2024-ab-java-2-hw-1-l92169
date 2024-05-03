package com.example.controller;

import com.example.domain.enums.FILTER;
import com.example.dto.response.UiSuccessContainer;
import com.example.service.ImageFiltersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
@Tag(name = "Image Filters Controller", description = "Базовый CRUD API для работы с пользовательскими запросами на редактирование картинок")
public class ImageFiltersController {

  private final ImageFiltersService service;

  @Operation(summary = "Применение указанных фильтров к изображению")
  @PostMapping(value = "/image/{image-id}/filters/apply", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Успех выполнения операции"),
      @ApiResponse(responseCode = "404", description = "Файл не найден в системе или недоступен",
          content = {
              @Content(mediaType = "application/json",
                  schema = @Schema(implementation = UiSuccessContainer.class))}),
      @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка",
          content = {
              @Content(mediaType = "application/json",
                  schema = @Schema(implementation = UiSuccessContainer.class))})})
  private ResponseEntity<?> applyImageFilters(
      @PathVariable(required = true, name = "image-id") UUID imageId,
      @RequestParam List<FILTER> filters) {
    return ResponseEntity.ok(service.applyImageFilters(imageId, filters));
  }

  @Operation(summary = "Получение ИД измененного файла по ИД запроса")
  @PostMapping(value = "/image/{image-id}/filters/{request-id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Успех выполнения операции"),
      @ApiResponse(responseCode = "404", description = "Файл не найден в системе или недоступен",
          content = {
              @Content(mediaType = "application/json",
                  schema = @Schema(implementation = UiSuccessContainer.class))}),
      @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка",
          content = {
              @Content(mediaType = "application/json",
                  schema = @Schema(implementation = UiSuccessContainer.class))})})
  private ResponseEntity<?> getModifiedImageByRequestId(
      @PathVariable(required = true, name = "image-id") UUID imageId,
      @PathVariable(required = true, name = "request-id") UUID requestId) {
    return ResponseEntity.ok(service.getModifiedImageByRequestId(imageId, requestId));
  }
}
