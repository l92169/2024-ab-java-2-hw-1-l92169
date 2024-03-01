package com.example.controller;

import com.example.domain.Operation;
import com.example.dto.OperationDto;
import com.example.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService service;

    @GetMapping("/operation/{type}")
    public List<OperationDto> getOperations(@PathVariable Operation.OperationType type) {
        return service.getOperationByType(type);
    }
}
