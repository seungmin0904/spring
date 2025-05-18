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

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
    );

    // 인증 성공 후 사용자 정보 가져오기
    MemberSecurityDTO principal = (MemberSecurityDTO) authentication.getPrincipal();

    // 원래 Member 엔티티가 필요하면 repository로 다시 불러오기
    return memberRepository.findByUsername(principal.getUsername())
        .orElseThrow(() -> new IllegalStateException("회원 정보 없음"));
   }
}
