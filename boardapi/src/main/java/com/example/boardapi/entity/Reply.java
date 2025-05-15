package com.example.boardapi.entity;

import jakarta.persistence.*;
import lombok.*;

import com.example.boardapi.base.Base;
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"board", "parent", "member"})

public class Reply extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reply parent;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private String text;

    public void updateText(String newText) {
        this.text = newText;
    }
}
