package com.example.boardapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardapi.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
