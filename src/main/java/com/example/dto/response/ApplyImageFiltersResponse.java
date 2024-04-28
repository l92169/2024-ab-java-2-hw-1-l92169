package com.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyImageFiltersResponse {
  @Schema(description = "ИД запроса в системе", required = true)
  private UUID requestId;
}
