package com.example.boardapi.service;

import com.example.boardapi.entity.Member;
import com.example.boardapi.repository.MemberRepository;
import com.example.boardapi.security.dto.MemberSecurityDTO;
import com.example.boardapi.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketAuthService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public UsernamePasswordAuthenticationToken authenticate(String token, String name) {
        // JWT 검증 및 username 추출
        String username = jwtUtil.validateAndGetUsername(token);
        if (username == null) {
            throw new IllegalArgumentException("JWT가 유효하지 않습니다.");
        }

        // DB에서 사용자 정보 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        if (!member.getName().equals(name)) {
            throw new IllegalArgumentException("이름 불일치");
        }
        // 인증객체 반환
        return new UsernamePasswordAuthenticationToken(
                MemberSecurityDTO.from(member),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
