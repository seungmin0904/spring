package com.example.boardapi.dto;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BoardRequestDTO {
    private String title;
    private String content;
    private String username; // 작성자 식별용 
}
