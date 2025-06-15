package com.example.boardapi.listener;

import com.example.boardapi.service.UserStatusService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener implements MessageListener {

    private final UserStatusService userStatusService;

    public RedisKeyExpirationListener(UserStatusService userStatusService) {
        this.userStatusService = userStatusService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        // 만료된 키가 user:status:{username} 패턴일 때만 처리
        if (expiredKey.startsWith("user:status:")) {
            String username = expiredKey.substring("user:status:".length());
            userStatusService.markOffline(username);
        }
    }
}
