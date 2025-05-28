package com.example.boardapi.service;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.boardapi.dto.ChatMessageDTO;
import com.example.boardapi.entity.ChatMessage;
import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.Member;
import com.example.boardapi.mapper.ChatMessageMapper;
import com.example.boardapi.repository.ChatMessageRepository;
import com.example.boardapi.repository.ChatRoomRepository;
import com.example.boardapi.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    public ChatMessageDTO handleMessage(Long roomId, ChatMessageDTO dto, SimpMessageHeaderAccessor accessor) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        Member member = memberRepository.findByname(dto.getSenderName())
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // 매퍼 활용
        ChatMessage message = ChatMessageMapper.toEntity(dto, room, member);
        chatMessageRepository.save(message);

        return ChatMessageMapper.toDTO(message);
    }
}
