package com.example.boardweb.board.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ReplyWebDTO {

  private Long parentRno; // 부모 댓글 번호
  private Long rno;
  private Long bno; // 게시글 번호
  private String replyer;
  private String text;
  private LocalDateTime createdDate;
  private Boolean deleted;
  private LocalDateTime moDateTime;

  private List<ReplyWebDTO> children = new ArrayList<>(); // 대댓글 리스트
}
