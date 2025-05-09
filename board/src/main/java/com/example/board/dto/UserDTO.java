package com.example.board.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString


public class UserDTO {

    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    @NotBlank(message = "아이디를 확인해주세요")
    private Long userId;
    
    
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{13,}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 13자 이상 입력해야 합니다")
    @NotBlank(message = "비밀번호를 확인해주세요")
    private String userPw;
    @Pattern(regexp = "^[a-zA-Z0-9]{3,10}$|^[가-힣]{2,4}$", message = "이름은 한글 2~4자 또는 영문 3~10자로 입력해야 합니다")
    @NotBlank(message = "이름을 확인해주세요")
    private String userName;
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @NotBlank(message = "이메일을 확인해주세요")
    @Size(max = 254, message = "이메일은 254자 이내로 입력해야 합니다")
    private String userEmail;
}
