package com.example.boardapi.controller;

import com.example.boardapi.dto.LoginRequestDTO;
import com.example.boardapi.dto.MemberRequestDTO;
import com.example.boardapi.dto.MemberResponseDTO;
import com.example.boardapi.entity.Member;
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
    // 회원가입 (POST /api/members/register)
    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberRequestDTO dto) {
        MemberResponseDTO response = memberService.register(dto);
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
        "name", member.getName()
    ));
  }

  @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal MemberSecurityDTO authUser) {
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        return ResponseEntity.ok(Map.of(
            "username", authUser.getUsername(),
            "name", authUser.getName()
        ));
    }
}