package com.example.boardapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String type;
    private Long serverId;
    private String serverName;
    // 필요하면 ownerName 등 추가
}
