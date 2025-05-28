package com.example.boardapi.entity;

import jakarta.persistence.*;
import com.example.boardapi.base.Base;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 방 이름
    private String type; // 지역/테마/관심사 등
    private String code; // 입장코드(공개방은 null)
    private String description; // 방 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Member createdBy; // 생성자(방장)
}
