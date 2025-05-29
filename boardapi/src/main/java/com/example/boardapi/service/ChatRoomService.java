package com.example.boardapi.service;

import com.example.boardapi.dto.ChatRoomCreateRequestDTO;
import com.example.boardapi.dto.ChatRoomDTO;
import com.example.boardapi.entity.*;
import com.example.boardapi.mapper.ChatRoomMapper;
import com.example.boardapi.repository.ChatRoomMemberRepository;
import com.example.boardapi.repository.ChatRoomRepository;
import com.example.boardapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ChatRoomDTO createChatRoom(String name, ChatRoomCreateRequestDTO request) {
        Member member = memberRepository.findByname(name)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        String code = generateInviteCode();

        ChatRoom room = ChatRoomMapper.toEntity(
                request,
                member,
                code,
                ChatRoomType.GROUP);

        chatRoomRepository.save(room);

        // 방장으로 참여자 등록
        ChatRoomMember roomMember = ChatRoomMember.builder()
                .room(room)
                .member(member)
                .isOwner(true)
                .joinedAt(java.time.LocalDateTime.now())
                .build();

        chatRoomMemberRepository.save(roomMember);

        return ChatRoomMapper.toDTO(room);
    }

    @Transactional
    public void joinChatRoom(String name, String code) {
        ChatRoom room = chatRoomRepository.findAll()
                .stream()
                .filter(r -> r.getCode() != null && r.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 초대코드"));

        // 이미 참여했으면 중복등록 방지
        Member member = memberRepository.findByname(name)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        boolean alreadyJoined = chatRoomMemberRepository.existsByRoomIdAndMemberMno(room.getId(), member.getMno());
        if (alreadyJoined)
            return;

        ChatRoomMember roomMember = ChatRoomMember.builder()
                .room(room)
                .member(member)
                .isOwner(false)
                .joinedAt(java.time.LocalDateTime.now())
                .build();

        chatRoomMemberRepository.save(roomMember);
    }

    @Transactional(readOnly = true)
    public boolean isMemberInRoom(Long roomId, String name) {
        Member member = memberRepository.findByname(name)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return chatRoomMemberRepository.existsByRoomIdAndMemberMno(roomId, member.getMno());
    }

    // 초대코드 생성 (간단히 UUID로)
    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDTO> getAllChatRooms() {
        List<ChatRoom> rooms = chatRoomRepository.findAll();
        return rooms.stream()
                .map(ChatRoomMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChatRoomDTO getChatRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        return ChatRoomMapper.toDTO(room);
    }

    @Transactional
    public void leaveChatRoom(Long roomId, String name) {
        Member member = memberRepository.findByname(name)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        ChatRoomMember roomMember = chatRoomMemberRepository.findByRoomIdAndMemberMno(roomId, member.getMno())
                .orElseThrow(() -> new IllegalArgumentException("참여 정보 없음"));

        chatRoomMemberRepository.delete(roomMember);
    }

    @Transactional(readOnly = true)
    public List<String> getChatRoomMembers(Long roomId) {
        List<ChatRoomMember> roomMembers = chatRoomMemberRepository.findByRoomId(roomId);
        return roomMembers.stream()
                .map(rm -> rm.getMember().getName()) // Member의 name만 반환
                .toList();
    }
}
