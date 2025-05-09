package com.example.boardweb.board.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@ToString 
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class BoardWebDTO {

    // entity 필드
    private Long bno;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    // 게시글 작성자
    private String email;
    private String name;

    // 댓글개수
    private Long replyCount;
    // 날짜
    private LocalDateTime crDateTime;
    private LocalDateTime moDateTime;

    @Builder.Default
    private List<ReplyWebDTO> replies = new ArrayList<>(); // 댓글 리스트
}
