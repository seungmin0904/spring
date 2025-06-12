package com.example.boardapi.config;

import com.example.boardapi.security.util.JwtUtil;
import com.example.boardapi.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserStatusService userStatusService;
    private final JwtUtil jwtUtil;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // 1. Header에서 직접 JWT/username을 꺼낸다.
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String jwt = accessor.getFirstNativeHeader("Authorization");
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }
        String username = null;
        if (jwt != null) {
            username = jwtUtil.validateAndGetUsername(jwt);
        }
        if (username != null) {
            userStatusService.userConnected(username);
            log.info("[RabbitMQ] WebSocket CONNECT username: {}", username);
        } else {
            log.warn("[RabbitMQ] WebSocket CONNECT - JWT 검증 실패 또는 토큰 없음");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = (String) headers.getSessionAttributes().get("username");
        if (username != null) {
            log.info("User Disconnected: {}", username);
            userStatusService.userDisconnected(username);
        }
    }
}
