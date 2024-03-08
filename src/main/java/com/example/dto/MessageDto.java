package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageDto implements Serializable {
    private String author;
    private String content;
    private LocalDateTime lastModifiedDate;
}
