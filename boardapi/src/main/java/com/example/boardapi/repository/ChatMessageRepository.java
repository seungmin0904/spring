package com.example.boardapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardapi.entity.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByRoomIdOrderBySentAtAsc(String roomId);
}
