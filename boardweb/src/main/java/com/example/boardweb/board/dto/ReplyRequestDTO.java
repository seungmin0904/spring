package com.example.boardweb.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyRequestDTO {
    private Long rno; // 댓글 번호
    private Long bno; // 게시글 번호
    private Long parentRno; // 부모 댓글 번호
    private String replyer; // 댓글 작성자
    private String username;
    @NotBlank(message = "댓글을 입력해주세요.")
    private String text; // 댓글 내용
}
