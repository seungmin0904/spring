package com.example.boardapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        // 1:1 DM방 중복 체크 (자유롭게 구현)
        ChatRoom dmRoom = chatRoomRepository.findDmRoomBetween(memberAId, memberBId)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.builder()
                            .name("DM-" + memberAId + "-" + memberBId)
                            .roomType(ChatRoomType.DM)
                            .type(ChannelType.TEXT)
                            .server(null)
                            .build();
                    chatRoomRepository.save(room);

                    ChatRoomMember m1 = ChatRoomMember.builder()
                            .chatRoom(room).member(memberRepository.findById(memberAId).orElseThrow()).build();
                    ChatRoomMember m2 = ChatRoomMember.builder()
                            .chatRoom(room).member(memberRepository.findById(memberBId).orElseThrow()).build();
                    chatRoomMemberRepository.save(m1);
                    chatRoomMemberRepository.save(m2);

                    return room;
                });
        return dmRoom;
    }

    // 내 DM방 리스트
    public List<ChatRoom> findMyDmRooms(Long memberId) {
        return chatRoomRepository.findByRoomTypeAndMembersMno(ChatRoomType.DM, memberId);
    }

    // DM방 참여자 리스트
    public List<Member> getMembers(Long roomId) {
        return chatRoomMemberRepository.findByChatRoomId(roomId)
                .stream().map(ChatRoomMember::getMember).toList();
    }
}
