package com.example.boardapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.boardapi.dto.ChatRoomResponseDTO;
import com.example.boardapi.entity.ChannelType;
import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.ChatRoomMember;
import com.example.boardapi.entity.ChatRoomType;
import com.example.boardapi.entity.Member;
import com.example.boardapi.repository.ChatRoomMemberRepository;
import com.example.boardapi.repository.ChatRoomRepository;
import com.example.boardapi.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DmRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;

    // 1:1 DM방 생성 또는 조회
    public ChatRoom getOrCreateDmRoom(Long memberAId, Long memberBId) {
        System.out.println("🔍 DM 생성 요청: memberAId=" + memberAId + ", memberBId=" + memberBId);

        if (memberAId == null || memberBId == null) {
            throw new IllegalArgumentException("memberAId, memberBId는 null일 수 없습니다.");
        }

        Long minId = Math.min(memberAId, memberBId);
        Long maxId = Math.max(memberAId, memberBId);

        boolean minUserExists = memberRepository.existsById(minId);
        boolean maxUserExists = memberRepository.existsById(maxId);
        System.out.println("✅ minId 존재?: " + minUserExists + ", maxId 존재?: " + maxUserExists);

        ChatRoom dmRoom = chatRoomRepository.findDmRoomBetween(minId, maxId)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.builder()
                            .name("DM-" + minId + "-" + maxId)
                            .roomType(ChatRoomType.DM)
                            .type(ChannelType.TEXT)
                            .server(null)
                            .build();
                    chatRoomRepository.save(room);

                    ChatRoomMember m1 = ChatRoomMember.builder()
                            .chatRoom(room)
                            .member(memberRepository.findById(minId)
                                    .orElseThrow(() -> new RuntimeException("❌ minId 유저 없음")))
                            .build();
                    ChatRoomMember m2 = ChatRoomMember.builder()
                            .chatRoom(room)
                            .member(memberRepository.findById(maxId)
                                    .orElseThrow(() -> new RuntimeException("❌ maxId 유저 없음")))
                            .build();
                    chatRoomMemberRepository.save(m1);
                    chatRoomMemberRepository.save(m2);

                    return room;
                });

        return dmRoom;
    }

    // 내 DM방 리스트
    // public List<ChatRoom> findMyDmRooms(Long memberId) {
    // return chatRoomRepository.findMyDmRooms(memberId);
    // }

    // DM방 참여자 리스트
    public List<Member> getMembers(Long roomId) {
        return chatRoomMemberRepository.findByChatRoomId(roomId)
                .stream().map(ChatRoomMember::getMember).toList();
    }

    public List<ChatRoomResponseDTO> findMyDmRooms(Long myId) {
        List<ChatRoom> rooms = chatRoomRepository.findByRoomTypeAndMembersMemberMno(ChatRoomType.DM, myId);

        return rooms.stream()
                .map(room -> ChatRoomResponseDTO.from(room, myId))
                .toList();
    }
}
