package com.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UiSuccessContainer {
  @Schema(description = "Признак успеха")
  private final Boolean success;
  @Schema(description = "Сообщение об ошибке", required = true)
  private String message;
}
