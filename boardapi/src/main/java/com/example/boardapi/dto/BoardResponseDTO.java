package com.example.boardapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardResponseDTO {

    private Long bno;
    private String title;
    private String content;
    private String writer;  // 작성자 출력용 
}
