package com.example.boardweb.security.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuspensionHistoryDTO {
    

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime liftedAt;
    private boolean manuallyLifted;
    private boolean permanent;
    private boolean active; // 현재 정지 상태인지 여부
    private String username; 
}
