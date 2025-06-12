package com.example.boardapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatusService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String ONLINE_USERS_KEY = "online_users";
    private static final long USER_TIMEOUT = 30; // 30초

    // 사용자 온라인 상태 등록
    public void userConnected(String username) {
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, username);
        redisTemplate.expire(ONLINE_USERS_KEY, USER_TIMEOUT, TimeUnit.SECONDS);
        broadcastOnlineUsers();
    }

    // 사용자 오프라인 상태로 변경
    public void userDisconnected(String username) {
        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, username);
        broadcastOnlineUsers();
    }

    // 온라인 사용자 목록 조회
    public Set<String> getOnlineUsers() {
        return redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
    }

    // 모든 클라이언트에게 온라인 사용자 목록 브로드캐스트
    private void broadcastOnlineUsers() {
        Set<String> onlineUsers = getOnlineUsers();
        log.info("[브로드캐스트] 현재 온라인유저 목록: {}", onlineUsers);
        messagingTemplate.convertAndSend("/topic/online-users", onlineUsers);
    }
}
