package com.example.boardapi.service;

import com.example.boardapi.dto.LoginRequestDTO;
import com.example.boardapi.dto.MemberRequestDTO;
import com.example.boardapi.dto.MemberResponseDTO;
import com.example.boardapi.entity.Member;
import com.example.boardapi.mapper.MemberMapper;
import com.example.boardapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;

    public MemberResponseDTO register(MemberRequestDTO dto) {
        if (memberRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        Member member = MemberMapper.toEntity(dto);
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        Member saved = memberRepository.save(member);
        return MemberMapper.toDTO(saved);
    }

    public MemberResponseDTO login(LoginRequestDTO dto) {
        // 1. 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()));

        // 2. SecurityContext에 수동 저장 (세션에 로그인 정보 등록됨)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 사용자 정보 응답
        Member member = memberRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return MemberMapper.toDTO(member);
    }
}
