package com.example.mapper;


import com.example.domain.Operation;
import com.example.dto.OperationDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OperationMapper {

    List<OperationDto> operationsToOperationDtos(List<Operation> operation);
    @Mapping(target = "id", expression = "java(null)")
    Operation operationDtoToOperation(OperationDto operationDto);
}
