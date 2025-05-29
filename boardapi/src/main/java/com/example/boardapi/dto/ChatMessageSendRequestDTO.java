package com.example.boardapi.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageSendRequestDTO {
    private Long roomId;
    private String content;
}
