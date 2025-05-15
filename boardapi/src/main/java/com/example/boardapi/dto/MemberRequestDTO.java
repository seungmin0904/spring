package com.example.boardapi.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDTO {
       private String username; // 이메일
    private String password;
    private String name;
}
