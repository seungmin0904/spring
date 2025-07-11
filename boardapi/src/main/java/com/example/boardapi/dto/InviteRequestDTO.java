package com.example.boardapi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InviteRequestDTO {

    private Long serverId;
    private LocalDateTime expireAt; // null이면 무제한
    private Integer maxUses; // null이면 무제한
    private String memo; // (옵션) 설명/라벨
}
