package com.example.demo.dto;


import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class MemberDTO {
  @Pattern(regexp = "(?=^[A-Za-z])(?=.+\\d)[A-Za-z.+\\d]{6,12}$", message = "아이디는 영대소문자,특수문자,숫자를 포함해서 6~12자리입니다.")
    @NotBlank(message = "userid 를 확인해 주세요")
    private String userid;

    @NotBlank(message = "password 를 확인해 주세요")
    private String password;

    @NotBlank(message = "이메일을 확인해 주세요")
    @Email(message = "이메일 형식을 확인해 주세요")
    private String email;

    // @Length(min = 2, max = 5)
    @Pattern(regexp = "^[가-힣]{2,5}$", message = "이름은 2~5 자리로 입력해야 합니다.")
    private String name;

    @jakarta.validation.constraints.NotNull(message = "나이는 필수요소입니다.")
    @Min(value = 0, message = "나이는 최소 0 이상이어야 합니다.")
    @Max(value = 140, message = "나이는 최대 140 이하여야 합니다.")
    private Integer age;

    @AssertTrue(message = "약관에 동의해야 가입할 수 있습니다.")
    private boolean check;

   // 타입	설명
   // boolean 기본형, 기본값은 false
   // Boolean 참조형, 기본값은 null, 그래서 @NotNull 같이 쓸 수도 있음
   
   // @NotNull(message = "동의 여부를 선택해 주세요.")
   // @AssertTrue(message = "약관에 동의해야 가입할 수 있습니다.")
   // private Boolean check;
}
