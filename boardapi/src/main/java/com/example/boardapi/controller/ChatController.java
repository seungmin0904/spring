package com.example.boardapi.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.example.boardapi.dto.ChatMessageDTO;
import com.example.boardapi.dto.ChatMessageSendRequestDTO;
import com.example.boardapi.entity.ChatMessage;
import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.mapper.ChatMessageMapper;
import com.example.boardapi.security.dto.MemberSecurityDTO;
import com.example.boardapi.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageSendRequestDTO request,
            @AuthenticationPrincipal MemberSecurityDTO member) {
        chatMessageService.sendMessage(member.getMno(), request, null);
    }

}
