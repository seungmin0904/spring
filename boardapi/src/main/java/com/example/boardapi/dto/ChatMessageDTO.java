package com.example.boardapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {

    private String senderName;

    @NotBlank(message = "내용을 입력하세요")
    private String userContent;

}
