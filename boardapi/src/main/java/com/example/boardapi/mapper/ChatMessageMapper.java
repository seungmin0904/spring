package com.example.boardapi.mapper;

import com.example.boardapi.dto.ChatMessageDTO;
import com.example.boardapi.entity.ChatMessage;
import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.Member;

public class ChatMessageMapper {
    // DTO → Entity 변환
    public static ChatMessage toEntity(ChatMessageDTO dto, ChatRoom room, Member member) {
        return ChatMessage.builder()
                .room(room)
                .member(member)
                .content(dto.getUserContent())
                .build();
    }

    // Entity → DTO 변환
    public static ChatMessageDTO toDTO(ChatMessage entity) {
        return ChatMessageDTO.builder()
                .senderName(entity.getMember().getName()) // 닉네임만 추출
                .userContent(entity.getContent())
                // .createdAt(entity.getCreatedAt().toString()) // 필요시 추가
                .build();
    }
}
