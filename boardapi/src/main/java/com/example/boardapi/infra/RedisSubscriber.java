// package com.example.boardapi.infra;

// import org.springframework.data.redis.connection.Message;
// import org.springframework.data.redis.connection.MessageListener;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Service;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class RedisSubscriber implements MessageListener {

// private final SimpMessagingTemplate messagingTemplate;

// @Override
// public void onMessage(Message message, byte[] pattern) {
// String msg = new String(message.getBody());
// log.info("🔔 Redis에서 수신: {}", msg);

// // 예: 모든 사용자에게 메시지 전송
// messagingTemplate.convertAndSend("/topic/global", msg);
// }
// }
