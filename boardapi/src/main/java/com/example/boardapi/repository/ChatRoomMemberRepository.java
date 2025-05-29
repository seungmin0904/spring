package com.example.boardapi.repository;

import com.example.boardapi.entity.ChatRoomMember;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    boolean existsByRoomIdAndMemberMno(Long roomId, Long membermno);

    Optional<ChatRoomMember> findByRoomIdAndMemberMno(Long roomId, Long membermno);

    List<ChatRoomMember> findByRoomId(Long roomId);
}
