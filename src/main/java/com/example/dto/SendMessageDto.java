package com.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SendMessageDto {
    private String author;
    private String content;
    private final LocalDateTime lastModifiedDate = LocalDateTime.now();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Integer> imageId;
}
