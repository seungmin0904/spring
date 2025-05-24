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
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

   // 회원가입: 이메일(=username)로 중복 체크, name(아이디/닉네임)은 별도 중복 체크
    public MemberResponseDTO register(MemberRequestDTO dto) {
        if (memberRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByname(dto.getName())) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }
        Member member = MemberMapper.toEntity(dto);
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        Member saved = memberRepository.save(member);
        return MemberMapper.toDTO(saved);
    }

    // 로그인: name(아이디/닉네임) + password
    public Member login(LoginRequestDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getName(), dto.getPassword()));

        MemberSecurityDTO principal = (MemberSecurityDTO) authentication.getPrincipal();

        // DB에서 name(닉네임)으로 조회
        return memberRepository.findByname(principal.getName())
                .orElseThrow(() -> new IllegalStateException("회원 정보 없음"));
    }

    // 닉네임 중복검사 (회원가입 시)
    public boolean isNicknameTaken(String name) {
        return memberRepository.existsByname(name);
    }

    // 닉네임 중복검사 (수정 시, 본인 제외)
    public boolean isNicknameTaken(String name, String currentUsername) {
        return memberRepository.findByname(name)
                .map(existing -> !existing.getUsername().equals(currentUsername))
                .orElse(false);
    }

    // 닉네임 변경 (username=이메일로 식별)
    public void updateNickname(String currentName, String newName) {
        Member member = memberRepository.findByname(currentName)
                .orElseThrow(() -> new UsernameNotFoundException("회원 없음"));

        if (isNicknameTaken(newName, currentName)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        member.setName(newName);
        memberRepository.save(member);
    }
    
    public Member getByName(String name) {
        log.debug("getByName 호출됨, name={}", name); // ★ 여기에 찍어!
        return memberRepository.findByname(name)
            .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));
    }

    // email로 아이디 찾기용 
    public Optional<String> findByNickname(String email) {
        return memberRepository.findByUsername(email)
                .map(Member::getName);
    }
}
