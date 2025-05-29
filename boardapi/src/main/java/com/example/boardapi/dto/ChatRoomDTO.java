package com.example.boardapi.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long id;
    private String name;
    private String code;
    private String type;
    private Long ownerId;
    private String ownerNickname;
}
