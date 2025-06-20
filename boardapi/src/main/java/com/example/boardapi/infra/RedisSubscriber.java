package com.example.boardapi.infra;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.boardapi.dto.FriendEvent;
import com.example.boardapi.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            FriendEvent event = objectMapper.readValue(body, FriendEvent.class);
            log.info("🔔 수신한 온라인 상태 이벤트: {}", event);

            // targetUserId → username 조회 필요
            String username = memberRepository.findUsernameById(event.getTargetUserId());

            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/friend",
                    event);

        } catch (Exception e) {
            log.error("친구 이벤트 수신 실패", e);
        }
    }
}
