package com.example.boardapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.boardapi.dto.ChatRoomCreateRequestDTO;
import com.example.boardapi.dto.ChatRoomDTO;
import com.example.boardapi.security.dto.MemberSecurityDTO;
import com.example.boardapi.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @PostMapping
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestBody ChatRoomCreateRequestDTO dto,
            @AuthenticationPrincipal MemberSecurityDTO member) {

        ChatRoomDTO createdRoom = chatRoomService.createChatRoom(member.getName(), dto);
        return ResponseEntity.ok(createdRoom);
    }

    // 채팅방 리스트 조회
    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getChatRooms() {
        List<ChatRoomDTO> chatRooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(chatRooms);
    }

    // 특정 채팅방 상세 조회
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomDTO> getChatRoom(@PathVariable Long roomId) {
        ChatRoomDTO chatRoom = chatRoomService.getChatRoom(roomId);
        return ResponseEntity.ok(chatRoom);
    }

    // 채팅방 참여
    @PostMapping("/{code}/join")
    public ResponseEntity<Void> joinChatRoom(@PathVariable String code,
            @AuthenticationPrincipal MemberSecurityDTO member) {

        chatRoomService.joinChatRoom(member.getName(), code);
        return ResponseEntity.ok().build();
    }

    // 채팅방 나가기
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long roomId,
            @AuthenticationPrincipal MemberSecurityDTO member) {

        chatRoomService.leaveChatRoom(roomId, member.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomId}/members")
    public ResponseEntity<List<String>> getChatRoomMembers(@PathVariable Long roomId) {
        List<String> memberNames = chatRoomService.getChatRoomMembers(roomId);
        return ResponseEntity.ok(memberNames);
    }
}