package com.example.controller;

import com.example.dto.ImageDto;
import com.example.dto.MessageDto;
import com.example.dto.SendMessageDto;
import com.example.exceptions.MessageNotFoundException;
import com.example.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageResource {

    private final MessageService messageService;

    @GetMapping("/messages")
    public List<MessageDto> getMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/message/{id}")
    public MessageDto getMessage(@PathVariable int id) throws MessageNotFoundException {
        return messageService.getMessageById(id);
    }

    @GetMapping("/message/{id}/images")
    public List<ImageDto> getMessageImages(@PathVariable int id) throws MessageNotFoundException {
        return messageService.getMessageImages(id);
    }

    @PostMapping("/send")
    public SendMessageDto sendMessage(@RequestBody SendMessageDto messageDto) {
        return messageService.addMessage(messageDto);
    }
}