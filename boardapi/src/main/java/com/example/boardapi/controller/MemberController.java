package com.example.boardapi.controller;

import com.example.boardapi.dto.LoginRequestDTO;
import com.example.boardapi.dto.MemberRequestDTO;
import com.example.boardapi.dto.MemberResponseDTO;
import com.example.boardapi.service.MemberService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 (POST /api/members/register)
    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberRequestDTO dto) {
        MemberResponseDTO response = memberService.register(dto);
        return ResponseEntity.ok(response);
    }

    // 로그인 (POST /api/members/login)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        String token = memberService.login(dto);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
