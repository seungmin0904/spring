package com.example.boardapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.boardapi.entity.ChatMessageEntity;
import com.example.boardapi.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getChatMessages(@PathVariable String roomId) {
        List<ChatMessageEntity> messages = chatMessageService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }
}
