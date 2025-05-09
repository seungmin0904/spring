package com.example.boardweb.oauth.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

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
public class OAuthUserDTO implements OAuth2User {
    private String provider; // kakao, google, naver
    private String providerId; // 소셜 사용자 고유 ID
    private String name; // 이름
    private String email; // 이메일
    private String profileImage; // 프로필 이미지 URL
    private String role; // ROLE_USER 등 권한

    private Map<String, Object> attributes; // OAuth2User의 속성 정보

    // OAuth2User 필수 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getName() {

        return name;
    }

    public String getUsername() {
        return email != null ? email : provider + "_" + providerId;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
