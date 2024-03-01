package com.example.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@AllArgsConstructor
public class Operation {
    @Id
    private String id;
    private String content;
    private LocalDateTime date;
    private OperationType type;

    public enum OperationType {
        WRITE, READ;
    }
}
