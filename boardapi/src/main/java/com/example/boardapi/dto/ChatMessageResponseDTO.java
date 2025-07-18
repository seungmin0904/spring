package com.example.boardapi.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.example.boardapi.entity.ChatMessageEntity;

@Data
public class ChatMessageResponseDTO {

    private Long id; // 메시지 PK (key)
    private String sender; // 닉네임
    private String message; // 내용
    private LocalDateTime sentAt; // 보낸 시각

    public static ChatMessageResponseDTO from(ChatMessageEntity entity) {
        ChatMessageResponseDTO dto = new ChatMessageResponseDTO();
        dto.setId(entity.getId());
        dto.setSender(entity.getSender().getName()); // Member 닉네임 or 이름
        dto.setMessage(entity.getMessage());
        dto.setSentAt(entity.getSentAt());
        return dto;
    }
}
