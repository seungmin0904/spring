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

// spring:
// application:
// name: boardapi

// datasource:
// url: jdbc:oracle:thin:@localhost:1521:xe
// username: c##java
// password: 12345
// driver-class-name: oracle.jdbc.OracleDriver

// jpa:
// hibernate:
// ddl-auto: update
// properties:
// hibernate:
// format_sql: true
// highlight_sql: true

// thymeleaf:
// check-template-location: false # API 프로젝트라면 Thymeleaf 템플릿 경고 방지

// mail:
// host: smtp.gmail.com
// port: 587
// username: gogo90490@gmail.com # 너의 Gmail 주소
// password: wplbcvinpmypohrz # 발급받은 앱 비밀번호 (16자리)
// properties:
// mail:
// smtp:
// auth: true
// starttls:
// enable: true
// default-encoding: UTF-8

// servlet:
// multipart:
// max-file-size: 10MB
// max-request-size: 10MB

// # data:
// # redis: # ✅ spring.data.redis 로 변경!
// # host: localhost
// # port: 6379

// rabbitmq: # 👈 바로 여기 추가!
// host: localhost
// port: 5672
// username: guest
// password: guest

// file:
// upload-dir: c:/src/spring/upload

// logging:
// level:
// org:
// hibernate:
// SQL: debug
// orm:
// jdbc:
// bind: trace
// "[org.springframework.security]": DEBUG

// jwt:
// secret: "my-super-secret-key-that-is-very-strong-2025" # 보안 키는 실제 환경에선 Base64
// 또는 길게!
// expiration: 3600000 # 1시간 (밀리초 기준) = 1000 * 60 * 60
