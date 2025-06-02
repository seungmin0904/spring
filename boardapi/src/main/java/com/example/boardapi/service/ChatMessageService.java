package com.example.boardapi.service;

import com.example.boardapi.entity.ChatMessageEntity;
import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.Member;
import com.example.boardapi.repository.ChatMessageRepository;
import com.example.boardapi.repository.ChatRoomRepository;
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
    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 메시지 조회
    public List<ChatMessageEntity> getMessagesByRoomId(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        return chatMessageRepository.findByRoomOrderBySentAtAsc(chatRoom);

    }

    public void handleMessage(Long roomId, String message, String username) {
        // DB에 저장
        Member sender = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .room(chatRoom)
                .message(message)
                .sentAt(LocalDateTime.now())
                .sender(sender)
                .build();
        chatMessageRepository.save(chatMessage);

    }
}
