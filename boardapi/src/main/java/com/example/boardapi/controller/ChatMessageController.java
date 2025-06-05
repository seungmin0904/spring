package com.example.boardapi.controller;

import com.example.boardapi.dto.ChatMessageResponseDTO;
import com.example.boardapi.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat") // 프론트랑 맞춰서 prefix 조정
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    // 채팅방 메시지 전체 조회 (roomId별)
    @GetMapping("/{roomId}")
    public List<ChatMessageResponseDTO> getMessages(@PathVariable Long roomId) {
        return chatMessageService.getMessagesByRoomId(roomId)
                .stream()
                .map(ChatMessageResponseDTO::from)
                .toList();
    }
}
