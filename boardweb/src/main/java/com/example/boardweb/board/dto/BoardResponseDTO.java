package com.example.boardweb.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseDTO {

    private Long bno;

    private String title;
    private String content;
    private String writer;
    private String username;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private boolean deleted;

    @Builder.Default
    private List<ReplyWebDTO> replies = List.of();
}
