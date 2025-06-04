package com.example.boardapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardapi.entity.ChatRoomMember;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findByChatRoomId(Long chatRoomId);

    List<ChatRoomMember> findByMemberMno(Long memberId);

    boolean existsByChatRoomIdAndMemberMno(Long chatRoomId, Long memberId);
}
