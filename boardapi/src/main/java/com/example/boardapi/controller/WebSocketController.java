package com.example.boardapi.controller;

import com.example.boardapi.dto.ChatMessageDTO;
import com.example.boardapi.service.ChatService;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatService chatService;

    // 클라이언트가 /app/chat.sendMessage/{roomId}로 메시지 발행
    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/room.{roomId}") // 이 방을 구독 중인 모든 클라이언트에게 메시지 broadcast
    public ChatMessageDTO sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatMessageDTO messageDTO,
            SimpMessageHeaderAccessor headerAccessor) {

        if (messageDTO.getUserContent() == null || messageDTO.getUserContent().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다.");
        }
        // (1) 실전에서는 DTO로 받고 서비스에서 엔티티 변환+DB 저장+권한/인증 처리
        // (2) 서비스에서 실제 메시지 저장 및 보낼 DTO 생성
        return chatService.handleMessage(roomId, messageDTO, headerAccessor);
    }
}
