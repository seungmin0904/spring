package com.example.boardapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 채널명 (1:1이면 null 가능)

    @Enumerated(EnumType.STRING)
    private ChatRoomType type; // PRIVATE, GROUP

    private String code; // 초대코드 (그룹채팅만)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner; // 방장 (Member)

    private LocalDateTime createdAt;
}
