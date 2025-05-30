// package com.example.boardapi.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.RedisConnectionFactory;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.listener.PatternTopic;
// import org.springframework.data.redis.listener.RedisMessageListenerContainer;

// import com.example.boardapi.infra.RedisSubscriber;

// import lombok.RequiredArgsConstructor;

// @Configuration
// @RequiredArgsConstructor
// public class RedisConfig {

// private final RedisSubscriber redisSubscriber;

// @Bean
// public RedisMessageListenerContainer redisContainer(RedisConnectionFactory
// connectionFactory) {
// RedisMessageListenerContainer container = new
// RedisMessageListenerContainer();
// container.setConnectionFactory(connectionFactory);
// container.addMessageListener(redisSubscriber, new
// PatternTopic("chatroom:*")); // 채팅방별 토픽
// return container;
// }

// @Bean
// public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory
// connectionFactory) {
// RedisTemplate<String, Object> template = new RedisTemplate<>();
// template.setConnectionFactory(connectionFactory);
// return template;
// }
// }
