package com.example.service;

import com.example.domain.Message;
import com.example.domain.Operation;
import com.example.dto.ImageDto;
import com.example.dto.MessageDto;
import com.example.dto.OperationDto;
import com.example.dto.SendMessageDto;
import com.example.exceptions.ImageNotFoundException;
import com.example.exceptions.MessageNotFoundException;
import com.example.mapper.ImagesMapper;
import com.example.mapper.MessageMapper;
import com.example.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository repository;

    private final ImagesMapper imageMapper;
    private final MessageMapper mapper;

    private final OperationService operationService;
    private final ImageService imageService;

    public List<MessageDto> getAllMessages() {
        return mapper.messagesToMessageDtos(repository.findAll());
    }

    @Cacheable(value = "MessageService::getMessageById", key = "#id")
    public MessageDto getMessageById(int id) throws MessageNotFoundException {
        Optional<Message> messageOptional = repository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new MessageNotFoundException("Message not found");
        }

        MessageDto message = mapper.messageToMessageDto(messageOptional.get());
        operationService.logOperation(
                new OperationDto(
                        String.format("Read message: %s", message),
                        LocalDateTime.now(),
                        Operation.OperationType.READ
                )
        );

        return message;
    }

    @Cacheable(value = "MessageService::getMessageImages", key = "#id" + ".images")
    public List<ImageDto> getMessageImages(int id) throws MessageNotFoundException {
        Optional<Message> messageOptional = repository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new MessageNotFoundException(id+"");
        }
        List<ImageDto> images = imageMapper.imagesToImageDtos(messageOptional.get().getImages());
        operationService.logOperation(new OperationDto(String.format("Read message images: %s", images),
                LocalDateTime.now(), Operation.OperationType.READ));
        return images;
    }

    @Cacheable(value = "MessageService::getMessageById", key = "#id", condition="#id!=null")
    public SendMessageDto addMessage(SendMessageDto messageDto) {
        List<Integer> imageId = messageDto.getImageId() != null ? messageDto.getImageId() : List.of();
        if (!imageId.isEmpty() && !imageService.existsAll(imageId)) {
            throw new ImageNotFoundException(imageId+"");
        }
        repository.save(mapper.sendMessageDtoToMessage(messageDto, imageService.getAllImages(imageId)));
        operationService.logOperation(new OperationDto(
                String.format("Send message: %s", messageDto),
                LocalDateTime.now(),
                Operation.OperationType.WRITE));
        return messageDto;
    }
}
