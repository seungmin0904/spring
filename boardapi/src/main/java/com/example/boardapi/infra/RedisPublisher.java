// package com.example.boardapi.infra;

// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Service;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class RedisPublisher {

// private final RedisTemplate<String, Object> redisTemplate;

// public void publish(String topic, String message) {
// log.info("📡 Redis로 메시지 발행: {}", message);
// redisTemplate.convertAndSend(topic, message);
// }
// }
