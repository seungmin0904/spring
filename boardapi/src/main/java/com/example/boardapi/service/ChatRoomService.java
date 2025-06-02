package com.example.boardapi.service;

import com.example.boardapi.entity.ChannelMember;
import com.example.boardapi.entity.ChannelRole;
import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.repository.ChannelMemberRepository;
import com.example.boardapi.repository.ChatMessageRepository;
import com.example.boardapi.repository.ChatRoomRepository;
import com.example.boardapi.security.custom.DuplicateChatRoomException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChannelMemberService channelMemberService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChannelMemberRepository channelMemberRepository;

    public ChatRoom createRoom(Long ownerId, String name, String description) {
        if (chatRoomRepository.findByName(name).isPresent())
            throw new DuplicateChatRoomException("이미 존재하는 채널명입니다.");

        String inviteCode = RandomStringUtils.randomAlphanumeric(8);
        ChatRoom room = chatRoomRepository.save(
                ChatRoom.builder()
                        .name(name)
                        .description(description)
                        .inviteCode(inviteCode)
                        .build());
        // 방장 자동 등록
        channelMemberService.joinChannel(ownerId, room.getId(), ChannelRole.ADMIN);
        return room;
    }

    public List<ChatRoom> listRooms() {
        return chatRoomRepository.findAll();
    }

    @Transactional
    public void deleteRoom(Long roomId, Long currentUserId) {
        // 현재 유저가 ADMIN인지 확인
        ChannelMember cm = channelMemberRepository.findByRoomIdAndMemberMno(roomId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 참여한 적이 없음"));

        if (cm.getRole() != ChannelRole.ADMIN) {
            throw new IllegalStateException("방장만 방을 삭제할 수 있습니다.");
        }

        // 1. 메시지 삭제
        chatMessageRepository.deleteByRoomId(roomId);

        // 2. 참여자 삭제
        channelMemberRepository.deleteByRoomId(roomId);

        // 3. 방 삭제
        chatRoomRepository.deleteById(roomId);
    }

    // 초대코드 조회
    public String getInviteCode(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        return room.getInviteCode();
    }

    // 초대코드로 채팅방 조회
    public ChatRoom getRoomByInviteCode(String inviteCode) {
        return chatRoomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대코드"));
    }
}
