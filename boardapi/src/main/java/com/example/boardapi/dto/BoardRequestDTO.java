package com.example.boardapi.dto;

import lombok.*;

@Setter
@Getter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
public class BoardRequestDTO {
    private String title;
    private String content;
    private String writer;

}
