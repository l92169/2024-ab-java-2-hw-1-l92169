package com.example.dto;

import com.example.domain.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationDto {
    private String content;
    private LocalDateTime date;
    private Operation.OperationType type;
}
