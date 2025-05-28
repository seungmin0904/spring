package com.example.boardapi.controller;

import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.Member;
import com.example.boardapi.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<List<ChatRoom>> list() {
        return ResponseEntity.ok(chatRoomService.findAll());
    }

    @PostMapping
    public ResponseEntity<ChatRoom> create(
            @RequestBody ChatRoom req,
            @AuthenticationPrincipal(expression = "member") Member member) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new IllegalArgumentException("채팅방 이름은 필수입니다.");
        }

        return ResponseEntity.ok(chatRoomService.create(req, member));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoom> get(@PathVariable Long roomId) {
        return ResponseEntity.of(chatRoomService.findById(roomId));
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<?> join(
            @PathVariable Long roomId,
            @RequestParam(required = false) String code,
            @AuthenticationPrincipal(expression = "member") Member member) {
        return chatRoomService.join(roomId, code, member);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ChatRoom>> myRooms(@AuthenticationPrincipal(expression = "member") Member member) {
        return ResponseEntity.ok(chatRoomService.findAllByMember(member));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal(expression = "member") Member currentUser) {
        chatRoomService.deleteRoom(roomId, currentUser); // Service 호출만!
        return ResponseEntity.ok("채팅방이 삭제되었습니다.");
    }
}
