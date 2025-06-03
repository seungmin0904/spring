package com.example.boardapi.controller;

import com.example.boardapi.dto.ServerRequestDTO;
import com.example.boardapi.dto.ServerResponseDTO;
import com.example.boardapi.security.dto.MemberSecurityDTO;
import com.example.boardapi.service.ServerService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    @PostMapping
    public ResponseEntity<ServerResponseDTO> createServer(
            @AuthenticationPrincipal MemberSecurityDTO member,
            @RequestBody ServerRequestDTO req) {
        if (member == null) {
            // 인증 안 된 유저 거부
            return ResponseEntity.status(401).build();
        }
        // 필요한 추가 검증(이메일 인증 등) 있으면 여기에
        ServerResponseDTO result = serverService.createServer(req, member.getMno());
        return ResponseEntity.ok(result);
    }
}
