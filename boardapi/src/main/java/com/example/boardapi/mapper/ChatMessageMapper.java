package com.example.boardapi.mapper;

import com.example.boardapi.dto.ChatMessageDTO;
import com.example.boardapi.dto.ChatMessageSendRequestDTO;
import com.example.boardapi.entity.ChatMessage;
import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.Member;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChatMessageMapper {
    // DTO → Entity
    public static ChatMessage toEntity(ChatMessageSendRequestDTO dto, ChatRoom room, Member sender, String imageUrl) {
        return ChatMessage.builder()
                .room(room)
                .sender(sender)
                .content(dto.getContent())
                .imageUrl(imageUrl) // 이미지 경로가 있으면 넣기
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Entity → DTO
    public static ChatMessageDTO toDTO(ChatMessage entity) {
        if (entity == null)
            return null;

        Member sender = entity.getSender();

        return ChatMessageDTO.builder()
                .id(entity.getId())
                .roomId(entity.getRoom().getId())
                .senderId(sender != null ? sender.getMno() : null)
                .senderNickname(sender != null ? sender.getName() : null)
                .senderProfileImage(sender != null ? sender.getProfile() : null)
                .content(entity.getContent())
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
