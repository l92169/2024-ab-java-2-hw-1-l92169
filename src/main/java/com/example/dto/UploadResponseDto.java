package com.example.dto;

import lombok.Data;

@Data
public class UploadResponseDto {
  private UploadDto result;
  private StatusDto status;
}
