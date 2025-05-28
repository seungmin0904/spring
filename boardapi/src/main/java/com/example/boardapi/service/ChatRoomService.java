package com.example.boardapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.ChatRoomMember;
import com.example.boardapi.entity.Member;
import com.example.boardapi.repository.ChatRoomMemberRepository;
import com.example.boardapi.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom create(ChatRoom req, Member member) {
        req.setCreatedBy(member);
        ChatRoom saved = chatRoomRepository.save(req);
        chatRoomMemberRepository.save(ChatRoomMember.builder()
                .room(saved)
                .member(member)
                .build());
        return saved;
    }

    public Optional<ChatRoom> findById(Long roomId) {
        return chatRoomRepository.findById(roomId);
    }

    public ResponseEntity<?> join(Long roomId, String code, Member member) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        if (room.getCode() != null && !room.getCode().isBlank()) {
            if (!room.getCode().equals(code)) {
                return ResponseEntity.status(403).body("코드 불일치");
            }
        }
        if (chatRoomMemberRepository.findByRoomAndMember(room, member).isEmpty()) {
            chatRoomMemberRepository.save(ChatRoomMember.builder()
                    .room(room)
                    .member(member)
                    .build());
        }
        return ResponseEntity.ok("입장 완료");
    }

    public List<ChatRoom> findAllByMember(Member member) {
        List<ChatRoomMember> members = chatRoomMemberRepository.findAllByMember(member);
        return members.stream().map(ChatRoomMember::getRoom).toList();
    }

    public void deleteRoom(Long roomId, Member currentUser) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        if (!room.getCreatedBy().getMno().equals(currentUser.getMno())) {
            throw new AccessDeniedException("방장만 삭제할 수 있습니다.");
        }
        chatRoomRepository.delete(room);
    }
}
