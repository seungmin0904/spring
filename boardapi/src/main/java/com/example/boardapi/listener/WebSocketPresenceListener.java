package com.example.boardapi.listener;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.boardapi.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketPresenceListener {

    private final @Qualifier("redisTemplate") RedisTemplate<String, String> redis;
    private final SimpMessagingTemplate broker;
    private final UserStatusService userStatusService;

    // 연결 시
    @EventListener
    public void onConnected(SessionConnectedEvent ev) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(ev.getMessage());
        String user = sha.getUser().getName();
        String sessionId = sha.getSessionId();
        String sessionsKey = "user:" + user + ":sessions";

        redis.opsForSet().add(sessionsKey, sessionId);
        Long cnt = redis.opsForSet().size(sessionsKey);

        if (cnt != null && cnt == 1) {
            redis.opsForSet().add("online_users", user);

            // 친구에게만 상태 전파
            List<String> friends = userStatusService.getFriendUsernames(user);
            for (String friend : friends) {
                broker.convertAndSendToUser(friend, "/queue/status",
                        Map.of("username", user, "status", "ONLINE"));
            }
        }
    }

    @EventListener
    public void onDisconnected(SessionDisconnectEvent ev) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(ev.getMessage());
        String user = sha.getUser().getName();
        String sessionId = sha.getSessionId();
        String sessionsKey = "user:" + user + ":sessions";

        redis.opsForSet().remove(sessionsKey, sessionId);
        Long cnt = redis.opsForSet().size(sessionsKey);

        if (cnt == null || cnt == 0) {
            redis.opsForSet().remove("online_users", user);

            // 친구에게만 상태 전파
            List<String> friends = userStatusService.getFriendUsernames(user);
            for (String friend : friends) {
                broker.convertAndSendToUser(friend, "/queue/status",
                        Map.of("username", user, "status", "OFFLINE"));
            }
        }
    }
}