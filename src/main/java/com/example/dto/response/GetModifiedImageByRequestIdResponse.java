package com.example.dto.response;

import com.example.domain.enums.STATUS;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetModifiedImageByRequestIdResponse {
  @Schema(description = "ИД модифицированного или оригинального файла в случае отсутствия первого",
      required = true)
  private UUID imageId;
  @Schema(description = "Статус обработки файла", required = true)
  private STATUS status;
}
