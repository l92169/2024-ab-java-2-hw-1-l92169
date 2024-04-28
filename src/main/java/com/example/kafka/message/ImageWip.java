package com.example.kafka.message;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImageWip {
  private UUID imageId;
  private UUID requestId;
  private List<String> filters;
}
