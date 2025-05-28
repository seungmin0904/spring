package com.example.boardapi.repository;

import com.example.boardapi.entity.ChatRoomMember;
import com.example.boardapi.entity.ChatRoom;
import com.example.boardapi.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    Optional<ChatRoomMember> findByRoomAndMember(ChatRoom room, Member member);

    List<ChatRoomMember> findAllByMember(Member member);
}
