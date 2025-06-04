package com.example.boardapi.dto;

import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.ChatRoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDTO {

    private Long id;
    private String name;
    private String description;
    private ChatRoomType roomType;
    private Long serverId;
    private String serverName;
    // 필요하면 ownerName 등 추가

    public static ChatRoomResponseDTO from(ChatRoom room) {
        return ChatRoomResponseDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .roomType(room.getRoomType())
                .build();
    }
}
