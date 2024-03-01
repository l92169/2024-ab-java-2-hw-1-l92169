package com.example.service;

import com.example.domain.Operation;
import com.example.dto.OperationDto;
import com.example.mapper.OperationMapper;
import com.example.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository repository;
    private final OperationMapper mapper;

    public void logOperation(OperationDto opetionDto) {
        repository.save(mapper.operationDtoToOperation(opetionDto));
    }

    public List<OperationDto> getOperationByType(Operation.OperationType type) {
        return mapper.operationsToOperationDtos(repository.findAllByType(type));
    }
}
