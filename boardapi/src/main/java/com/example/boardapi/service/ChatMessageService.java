package com.example.boardapi.service;

import com.example.boardapi.entity.ChatMessageEntity;
import com.example.boardapi.entity.Member;
import com.example.boardapi.repository.ChatMessageRepository;
import com.example.boardapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 채팅방 메시지 조회
    public List<ChatMessageEntity> getMessagesByRoomId(String roomId) {
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    public void handleMessage(String roomId, String message, String username) {
        // DB에 저장
        Member sender = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .roomId(roomId)
                .message(message)
                .sentAt(LocalDateTime.now())
                .sender(sender)
                .build();
        chatMessageRepository.save(chatMessage);

        // Redis에 발행
        messagingTemplate.convertAndSend("/topic/chatroom." + roomId, message);
    }
}
