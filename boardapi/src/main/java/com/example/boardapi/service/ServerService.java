package com.example.boardapi.service;

import com.example.boardapi.dto.ServerRequestDTO;
import com.example.boardapi.dto.ServerResponseDTO;
import com.example.boardapi.entity.Server;
import com.example.boardapi.entity.Member;
import com.example.boardapi.entity.ServerMember;
import com.example.boardapi.entity.ServerRole;
import com.example.boardapi.repository.ServerRepository;
import com.example.boardapi.repository.ServerMemberRepository;
import com.example.boardapi.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ServerResponseDTO createServer(ServerRequestDTO dto, Long ownerId) {
        Member owner = memberRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Server server = Server.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .owner(owner)
                .build();
        serverRepository.save(server);

        // 서버 개설자에게 ADMIN 권한 부여
        ServerMember serverMember = ServerMember.builder()
                .member(owner)
                .server(server)
                .role(ServerRole.ADMIN)
                .build();
        serverMemberRepository.save(serverMember);

        return ServerResponseDTO.from(server);
    }
}
