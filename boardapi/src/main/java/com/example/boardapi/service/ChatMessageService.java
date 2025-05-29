package com.example.boardapi.service;

import com.example.boardapi.dto.ChatMessageDTO;
import com.example.boardapi.dto.ChatMessageSendRequestDTO;
import com.example.boardapi.entity.*;
import com.example.boardapi.mapper.ChatMessageMapper;
import com.example.boardapi.repository.ChatMessageRepository;
import com.example.boardapi.repository.ChatRoomMemberRepository;
import com.example.boardapi.repository.ChatRoomRepository;
import com.example.boardapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final SimpMessagingTemplate messagingTemplate; // 실시간 전송!

    @Transactional
    public ChatMessageDTO sendMessage(Long memberId, ChatMessageSendRequestDTO request, String imageUrl) {
        // 1️⃣ 채팅방 참여자 검증
        if (!chatRoomMemberRepository.existsByRoomIdAndMemberMno(request.getRoomId(), memberId)) {
            throw new IllegalArgumentException("채팅방에 참여하지 않았습니다.");
        }

        // 2️⃣ 채팅방 & 전송자 조회
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        Member sender = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 3️⃣ 채팅 메시지 생성 & 저장
        ChatMessage message = ChatMessageMapper.toEntity(request, room, sender, imageUrl);
        chatMessageRepository.save(message);

        // 4️⃣ DTO 변환
        ChatMessageDTO dto = ChatMessageMapper.toDTO(message);

        // 5️⃣ 채팅방별 topic으로 실시간 전송
        messagingTemplate.convertAndSend("/topic/channel." + request.getRoomId(), dto);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getMessages(Long roomId, Long memberId) {
        // 1️⃣ 참여자 검증
        if (!chatRoomMemberRepository.existsByRoomIdAndMemberMno(roomId, memberId)) {
            throw new IllegalArgumentException("채팅방에 참여하지 않았습니다.");
        }

        // 2️⃣ 채팅방 메시지 조회
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);

        // 3️⃣ DTO로 변환 후 반환
        return messages.stream()
                .map(ChatMessageMapper::toDTO)
                .collect(Collectors.toList());
    }
}
