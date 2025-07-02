package com.example.boardapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardapi.entity.ChatRoomMember;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    // ✅ 특정 방에 속한 전체 멤버 조회
    List<ChatRoomMember> findByChatRoomId(Long chatRoomId);

    // ✅ 특정 유저의 모든 참여 채널 조회
    List<ChatRoomMember> findByMemberMno(Long memberId);

    // ✅ 특정 유저가 특정 방에 참여 중인지 여부
    boolean existsByChatRoomIdAndMemberMno(Long chatRoomId, Long memberId);

    // ✅ ✅ 추가: 특정 방 + 특정 유저 조회
    Optional<ChatRoomMember> findByChatRoomIdAndMemberMno(Long chatRoomId, Long memberId);

    // ✅ ✅ 추가: visible=true인 DM 목록 조회용
    List<ChatRoomMember> findByMemberMnoAndVisibleTrue(Long memberId);
}
