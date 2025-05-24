package com.example.boardapi.service;

import com.example.boardapi.dto.LoginRequestDTO;
import com.example.boardapi.dto.MemberRequestDTO;
import com.example.boardapi.dto.MemberResponseDTO;
import com.example.boardapi.entity.Member;
import com.example.boardapi.mapper.MemberMapper;
import com.example.boardapi.repository.MemberRepository;
import com.example.boardapi.security.dto.MemberSecurityDTO;
import com.example.boardapi.security.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public MemberResponseDTO register(MemberRequestDTO dto) {
        if (memberRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        Member member = MemberMapper.toEntity(dto);
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        Member saved = memberRepository.save(member);
        return MemberMapper.toDTO(saved);
    }

    public Member login(LoginRequestDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        // 인증 성공 후 사용자 정보 가져오기
        MemberSecurityDTO principal = (MemberSecurityDTO) authentication.getPrincipal();

        // 원래 Member 엔티티가 필요하면 repository로 다시 불러오기
        return memberRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("회원 정보 없음"));

    }

    // 중복검사
    // 비회원 가능 - 단순 중복 여부
    public boolean isNicknameTaken(String name) {
        return memberRepository.existsByname(name);
    }

    // 로그인 사용자 전용 - 자기 자신 제외
    public boolean isNicknameTaken(String name, String currentUsername) {
        return memberRepository.findByname(name)
                .map(existing -> !existing.getUsername().equals(currentUsername))
                .orElse(false);
    }

    public void updateNickname(String username, String name) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원 없음"));

        if (isNicknameTaken(name, username)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        member.setName(name);
        memberRepository.save(member);
    }

}
