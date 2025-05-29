package com.example.boardapi.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateRequestDTO {
    private String name; // 채널명 (1:1은 null)
}
