package com.example.boardweb.board.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.boardweb.security.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = {"memberWeb", "replies"})
@Table(name = "BOARD_TBL")
@Entity

public class BoardWeb extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 4000)
    private String content;
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    //게시글에 달려있는 댓글 정보 조회
    @Builder.Default
    @OneToMany(mappedBy = "boardWeb", fetch = FetchType.LAZY)
    private List<ReplyWeb> replies = new ArrayList<>();
}
