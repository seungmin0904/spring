package com.example.boardapi.dto;

import java.time.LocalDateTime;

import com.example.boardapi.entity.Notification;
import com.example.boardapi.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// boardapi/src/main/java/com/example/boardapi/dto/NotificationDTO.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private NotificationType type; // BOARD_REPLY, FRIEND_REQUEST, MENTION 등
    private String message; // 알림 메시지
    private String sender; // 알림을 발생시킨 사용자
    private String receiver; // 알림을 받을 사용자
    private Long referenceId; // 관련 게시글/댓글 ID
    private boolean isRead; // 읽음 여부
    private LocalDateTime createdAt;

    // Entity를 DTO로 변환하는 정적 메서드
    public static NotificationDTO from(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .sender(notification.getSender().getUsername())
                .receiver(notification.getReceiver().getUsername())
                .referenceId(notification.getReferenceId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}