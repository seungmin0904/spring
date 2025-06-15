package com.example.boardapi.listener;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketPresenceListener {

    private final @Qualifier("redisTemplate") RedisTemplate<String, String> redis;
    private final SimpMessagingTemplate broker;

    // 연결 시
    @EventListener
    public void onConnected(SessionConnectedEvent ev) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(ev.getMessage());
        String user = sha.getUser().getName();
        String sessionId = sha.getSessionId();

        String sessionsKey = "user:" + user + ":sessions";
        // 1) 이 세션을 기록
        redis.opsForSet().add(sessionsKey, sessionId);

        // 2) 만약 기존에 세션이 없었다면
        Long cnt = redis.opsForSet().size(sessionsKey);
        if (cnt != null && cnt == 1) {
            // “online_users” 세트에 추가
            redis.opsForSet().add("online_users", user);
            // 모든 친구에게 ONLINE 이벤트 발송
            broker.convertAndSend("/topic/online-users." + user,
                    Map.of("username", user, "status", "ONLINE"));
        }
    }

    // 끊김 시
    @EventListener
    public void onDisconnected(SessionDisconnectEvent ev) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(ev.getMessage());
        String user = sha.getUser().getName();
        String sessionId = sha.getSessionId();

        String sessionsKey = "user:" + user + ":sessions";
        // 1) 이 세션 제거
        redis.opsForSet().remove(sessionsKey, sessionId);

        // 2) 남은 세션이 0개가 되면
        Long cnt = redis.opsForSet().size(sessionsKey);
        if (cnt == null || cnt == 0) {
            // “online_users” 세트에서 제거
            redis.opsForSet().remove("online_users", user);
            broker.convertAndSend("/topic/online-users." + user,
                    Map.of("username", user, "status", "OFFLINE"));
        }
    }
}