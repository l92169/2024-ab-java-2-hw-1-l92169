package com.example.mapper;

import com.example.domain.Image;
import com.example.domain.Message;
import com.example.dto.MessageDto;
import com.example.dto.SendMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageMapper {
    List<MessageDto> messagesToMessageDtos(List<Message> messages);
    MessageDto messageToMessageDto(Message message);
    @Mapping(target = "images", source = "images")
    Message sendMessageDtoToMessage(SendMessageDto message, List<Image> images);
}
