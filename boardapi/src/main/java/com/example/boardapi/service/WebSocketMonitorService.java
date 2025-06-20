package com.example.boardapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketMonitorService {

    private final SimpUserRegistry simpUserRegistry;

    @Scheduled(fixedDelay = 10000)
    public void checkConnectedUsers() {
        simpUserRegistry.getUsers().forEach(user -> log.info("🧩 Connected WebSocket user: {}", user.getName()));
    }
}
