package com.example.boardapi.controller;

import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.security.dto.MemberSecurityDTO;
import com.example.boardapi.service.ChatRoomService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor

public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채널 생성
    @PostMapping
    public ChatRoom createRoom(
            @AuthenticationPrincipal MemberSecurityDTO member,
            @RequestBody Map<String, String> req) {

        String name = req.get("name");
        String description = req.get("description");
        return chatRoomService.createRoom(member.getMno(), name, description); // member.getMno() == ownerId
    }

    // 채널 목록 렌더링
    @GetMapping
    public List<ChatRoom> listRooms() {
        return chatRoomService.listRooms();
    }

    // 채널 삭제 (방장만 가능)
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal MemberSecurityDTO member) {

        chatRoomService.deleteRoom(roomId, member.getMno());
        return ResponseEntity.noContent().build();
    }

    // 초대코드 조회
    @GetMapping("/{roomId}/invite-code")
    public Map<String, String> getInviteCode(@PathVariable Long roomId) {
        String code = chatRoomService.getInviteCode(roomId);
        return Map.of("code", code);
    }

    // 초대코드로 채팅방 조회
    @GetMapping("/by-invite/{inviteCode}")
    public ChatRoom getRoomByInviteCode(@PathVariable String inviteCode) {
        return chatRoomService.getRoomByInviteCode(inviteCode);
    }
}
