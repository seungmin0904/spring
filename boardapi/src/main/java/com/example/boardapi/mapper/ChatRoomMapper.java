package com.example.boardapi.mapper;

import com.example.boardapi.dto.ChatRoomCreateRequestDTO;
import com.example.boardapi.dto.ChatRoomDTO;
import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.ChatRoomType;
import com.example.boardapi.entity.Member;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class ChatRoomMapper {

    // DTO → Entity
    public static ChatRoom toEntity(ChatRoomCreateRequestDTO dto, Member owner, String code, ChatRoomType type) {
        return ChatRoom.builder()
                .name(dto.getName())
                .code(code)
                .type(type)
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Entity → DTO
    public static ChatRoomDTO toDTO(ChatRoom entity) {
        if (entity == null)
            return null;

        Member owner = entity.getOwner();

        return ChatRoomDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .type(entity.getType().name())
                .ownerId(owner != null ? owner.getMno() : null)
                .ownerNickname(owner != null ? owner.getName() : null)
                .build();
    }
}
