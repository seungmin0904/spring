package com.example.boardweb.security.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class MemberSecurityDTO implements UserDetails {

    @NotBlank(message = "아이디는 필수입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 12, message = "비밀번호는 12자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&+=]).{12,}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    private List<GrantedAuthority> authorities;

    private boolean emailVerified; // 이메일 인증 여부

    private boolean suspended;

    private LocalDateTime suspendedUntil;

    private Set<String> roleNames;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (기본 true)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부 (기본 true)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부 (기본 true)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성 여부 (기본 true)
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    @Builder
    public MemberSecurityDTO(String username, String password, String name, List<GrantedAuthority> authorities,
            boolean emailVerified) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.authorities = authorities;
        this.emailVerified = emailVerified;
    }
}
