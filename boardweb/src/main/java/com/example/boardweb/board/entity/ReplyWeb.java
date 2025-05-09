package com.example.boardweb.board.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
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
@ToString(exclude = { "boardWeb", "parent", "children" })
@Entity
@EntityListeners(AuditingEntityListener.class) // JPA Auditing을 위한 리스너 설정
public class ReplyWeb extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;
    @Column(nullable = false)
    private String text;
    @Column(nullable = false)
    private String replyer;

    // 댓글 삭제 여부를 나타내는 필드
    // 기본값은 false로 설정하여 댓글이 삭제되지 않은 상태로 시작
    // 댓글이 삭제되면 deleted 필드를 true로 설정하여 논리적 삭제를 구현
    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    @JoinColumn(name = "board_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private BoardWeb boardWeb;

    // : 답글(Parent-Child) 관계 매핑
    // 대댓글을 위한 부모 댓글과의 관계 설정
    // 대댓글은 부모 댓글을 참조하고, 부모 댓글은 대댓글 리스트를 참조함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_rno")
    private ReplyWeb parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReplyWeb> children = new ArrayList<>(); // 대댓글 리스트
}
