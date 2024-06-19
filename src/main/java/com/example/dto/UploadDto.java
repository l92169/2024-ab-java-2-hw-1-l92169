package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadDto {
  @JsonProperty("upload_id")
  String uploadId;
}