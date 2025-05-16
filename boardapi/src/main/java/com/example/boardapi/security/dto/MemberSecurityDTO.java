package com.example.boardapi.security.dto;

import com.example.boardapi.entity.Member;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSecurityDTO implements UserDetails {
    private String username; // 이메일
    private String password;
    private String name;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();

    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static MemberSecurityDTO from(Member member) {
        return MemberSecurityDTO.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .name(member.getName())
                .build();
    }
}
