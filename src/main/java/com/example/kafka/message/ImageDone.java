package com.example.kafka.message;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImageDone {
  private UUID imageId;
  private UUID requestId;
}
