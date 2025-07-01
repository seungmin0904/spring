package com.example.boardapi.dto;

import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.ChatRoomMember;
import com.example.boardapi.entity.ChatRoomType;
import com.example.boardapi.entity.Member;

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
    private String type;
    private ChatRoomType roomType;
    private Long serverId;
    private String serverName;
    // 필요하면 ownerName 등 추가

    public static ChatRoomResponseDTO from(ChatRoom room) {
        return ChatRoomResponseDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .type(null != room.getType() ? room.getType().name() : null)
                .roomType(room.getRoomType())
                .build();
    }

    public static ChatRoomResponseDTO from(ChatRoom room, Long myId) {
        Member opponent = room.getMembers().stream()
                .map(ChatRoomMember::getMember)
                .filter(m -> !m.getMno().equals(myId))
                .findFirst()
                .orElse(null);

        return ChatRoomResponseDTO.builder()
                .id(room.getId())
                .name(opponent != null ? opponent.getName() : "상대 없음")
                .build();
    }
}
