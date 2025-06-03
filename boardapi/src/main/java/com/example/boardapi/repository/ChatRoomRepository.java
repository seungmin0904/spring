package com.example.boardapi.repository;

import com.example.boardapi.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 채팅방 이름으로 조회
    // Optional을 사용하여 이름이 없을 경우를 처리
    Optional<ChatRoom> findByName(String name);

}
