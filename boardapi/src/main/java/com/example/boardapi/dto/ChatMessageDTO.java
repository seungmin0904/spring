package com.example.boardapi.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderNickname;
    private String senderProfileImage;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
}
