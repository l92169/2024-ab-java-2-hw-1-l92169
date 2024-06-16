package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
  @Schema(description = "ИД файла", required = false, format = "uuid")
  private String imageId;

  @Schema(description = "Название изображения", required = true)
  private String filename;

  @Schema(description = "Размер файла в байтах", required = true, format = "int32")
  private int size;
}