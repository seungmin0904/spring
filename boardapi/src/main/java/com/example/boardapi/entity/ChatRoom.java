package com.example.boardapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Column(unique = true, nullable = false, length = 100)
    private String name; // 채팅방 이름 (중복불가) 가능여부는 나중에

    private String description; // 방 설명

    @Column(unique = true)
    private String inviteCode;

    // (양방향 옵션)
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<ChatMessageEntity> messages;
}
