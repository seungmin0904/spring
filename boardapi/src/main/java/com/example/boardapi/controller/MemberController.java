package com.example.boardapi.controller;

import com.example.boardapi.dto.LoginRequestDTO;
import com.example.boardapi.dto.MemberRequestDTO;
import com.example.boardapi.dto.MemberResponseDTO;
import com.example.boardapi.entity.Member;
import com.example.boardapi.repository.EmailVerificationTokenRepository;
import com.example.boardapi.security.dto.MemberSecurityDTO;
import com.example.boardapi.security.util.JwtUtil;
import com.example.boardapi.service.MemberService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final EmailVerificationTokenRepository tokenRepository;

    // 회원가입 (POST /api/members/register)
    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberRequestDTO dto) {
        boolean isVerified = tokenRepository
                .findByUsernameAndVerifiedTrue(dto.getUsername())
                .isPresent();
        if (!isVerified) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null); // 또는 커스텀 에러 DTO 반환 가능
        }
        MemberResponseDTO response = memberService.register(dto);
        tokenRepository.deleteByUsername(dto.getUsername());
        return ResponseEntity.ok(response);
    }

    // 로그인 (POST /api/members/login)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        Member member = memberService.login(dto);
        String token = jwtUtil.generateToken(member.getUsername());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", member.getUsername(),
                "name", member.getName()));
    }

    // 회원정보
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal MemberSecurityDTO authUser) {
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        return ResponseEntity.ok(Map.of(
                "username", authUser.getUsername(),
                "name", authUser.getName()));
    }

    // 중복검사
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname,
            @AuthenticationPrincipal MemberSecurityDTO member) {

        boolean isTaken = (member == null)
                ? memberService.isNicknameTaken(nickname) // 비회원
                : memberService.isNicknameTaken(nickname, member.getUsername()); // 회원

        return ResponseEntity.ok(isTaken);
    }

    @PutMapping("/nickname")
    public ResponseEntity<Void> updateNickname(@RequestBody Map<String, String> body,
            @AuthenticationPrincipal MemberSecurityDTO member) {
        String newNickname = body.get("nickname");
        memberService.updateNickname(member.getUsername(), newNickname);
        return ResponseEntity.ok().build();
    }
}