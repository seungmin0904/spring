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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DmRoomService {
        private final ChatRoomRepository chatRoomRepository;
        private final ChatRoomMemberRepository chatRoomMemberRepository;
        private final MemberRepository memberRepository;

        // 1:1 DM방 생성 또는 조회
        // ✅ 1:1 DM방 생성 또는 기존 방 조회 (중복 Unique Constraint 방지)
        public ChatRoom getOrCreateDmRoom(Long memberAId, Long memberBId) {
                System.out.println("🔍 DM 생성 요청: memberAId=" + memberAId + ", memberBId=" + memberBId);

                if (memberAId == null || memberBId == null) {
                        throw new IllegalArgumentException("❌ memberAId, memberBId는 null일 수 없습니다.");
                }

                Long minId = Math.min(memberAId, memberBId);
                Long maxId = Math.max(memberAId, memberBId);

                // ✅ 두 유저 존재 여부 체크 (DB 유효성 확인)
                boolean minUserExists = memberRepository.existsById(minId);
                boolean maxUserExists = memberRepository.existsById(maxId);
                System.out.println("✅ minId 존재?: " + minUserExists + ", maxId 존재?: " + maxUserExists);

                if (!minUserExists || !maxUserExists) {
                        throw new IllegalArgumentException(
                                        "❌ 대상 유저 중 하나가 존재하지 않습니다. minId=" + minId + ", maxId=" + maxId);
                }

                // ✅ 이미 존재하는 DM방 조회
                Optional<ChatRoom> existingRoom = chatRoomRepository.findDmRoomBetween(minId, maxId);
                if (existingRoom.isPresent()) {
                        System.out.println("✅ 기존 DM방 존재 → 기존 방 반환: roomId=" + existingRoom.get().getId());
                        return existingRoom.get();
                }

                // ✅ 새 방 생성
                ChatRoom newRoom = ChatRoom.builder()
                                .name("DM-" + minId + "-" + maxId) // ✅ 항상 두 유저 ID 기준 고정
                                .roomType(ChatRoomType.DM)
                                .type(ChannelType.TEXT)
                                .server(null)
                                .build();
                chatRoomRepository.save(newRoom);

                // ✅ 멤버 객체 가져오기
                Member minUser = memberRepository.findById(minId)
                                .orElseThrow(() -> new RuntimeException("❌ minId 유저 없음"));
                Member maxUser = memberRepository.findById(maxId)
                                .orElseThrow(() -> new RuntimeException("❌ maxId 유저 없음"));

                // ✅ 방 멤버 저장
                ChatRoomMember member1 = ChatRoomMember.builder()
                                .chatRoom(newRoom)
                                .member(minUser)
                                .build();

                ChatRoomMember member2 = ChatRoomMember.builder()
                                .chatRoom(newRoom)
                                .member(maxUser)
                                .build();

                chatRoomMemberRepository.save(member1);
                chatRoomMemberRepository.save(member2);

                System.out.println("✅ 새 DM방 생성 완료: roomId=" + newRoom.getId());
                return newRoom;
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
