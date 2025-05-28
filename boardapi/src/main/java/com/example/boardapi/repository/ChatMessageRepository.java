package com.example.boardapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardapi.entity.ChatMessage;
import com.example.boardapi.entity.ChatRoom;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomOrderByCreatedDateAsc(ChatRoom room);
}
