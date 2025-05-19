package com.example.boardapi.dto;

import lombok.Data;

@Data
public class EmailVerifyDTO {
    private String username;
    private String code;
}
