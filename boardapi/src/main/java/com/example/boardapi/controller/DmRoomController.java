package com.example.boardapi.controller;

import com.example.boardapi.dto.ChatRoomResponseDTO;
import com.example.boardapi.dto.DmRoomRequestDTO;
import com.example.boardapi.dto.MemberResponseDTO;
import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.Member;
import com.example.boardapi.mapper.MemberMapper;
import com.example.boardapi.service.DmRoomService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dm")
@RequiredArgsConstructor
public class DmRoomController {

    private final DmRoomService dmRoomService;

    // 1:1 DM방 생성 또는 조회
    @PostMapping("/room")
    public ChatRoomResponseDTO createOrGetDmRoom(@RequestBody DmRoomRequestDTO request) {
        ChatRoom room = dmRoomService.getOrCreateDmRoom(request.getMyId(), request.getFriendId());
        return ChatRoomResponseDTO.from(room);
    }

    // 내가 속한 DM방 리스트
    @GetMapping("/rooms/{memberId}")
    public List<ChatRoomResponseDTO> getMyDmRooms(@PathVariable Long memberId) {
        return dmRoomService.findMyDmRooms(memberId)
                .stream()
                .map(ChatRoomResponseDTO::from)
                .collect(Collectors.toList());
    }

    // DM방 참여자 리스트
    @GetMapping("/room/{roomId}/members")
    public List<MemberResponseDTO> getMembers(@PathVariable Long roomId) {
        List<Member> members = dmRoomService.getMembers(roomId);
        return members.stream().map(MemberMapper::toDTO).toList();
    }
}
