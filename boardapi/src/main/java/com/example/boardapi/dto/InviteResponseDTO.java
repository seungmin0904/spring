package com.example.boardapi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InviteResponseDTO {
    // 프론트에 보여줄 정보
    private String inviteCode;
    private Long serverId;
    private String serverName;
    private String creatorName;
    private LocalDateTime expireAt;
    private Integer maxUses;
    private Integer uses;
    private Boolean active;
    private String memo;
}
