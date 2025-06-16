package com.example.boardapi.config;

import com.example.boardapi.websocket.JwtHandshakeInterceptor;
import com.example.boardapi.security.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.*;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor handshakeInterceptor;
    private final JwtTokenProvider jwtTokenProvider;

    // 1) SockJS 엔드포인트에 HandshakeInterceptor 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws-chat")
                // .addInterceptors(handshakeInterceptor) // ← 여기
                .setAllowedOriginPatterns("*");

    }

    // 2) 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    // 3) CONNECT 프레임에 대한 JWT 검사용 ChannelInterceptor 등록
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor());
    }

    @Bean
    public ChannelInterceptor jwtChannelInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                var accessor = MessageHeaderAccessor
                        .getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    try {
                        String bearer = accessor.getFirstNativeHeader("Authorization");
                        if (bearer == null || !bearer.startsWith("Bearer ")) {
                            throw new IllegalArgumentException("Missing or invalid Authorization header");
                        }

                        String token = bearer.substring(7);
                        if (!jwtTokenProvider.validateToken(token)) {
                            throw new IllegalArgumentException("Invalid JWT token");
                        }

                        accessor.setUser(jwtTokenProvider.getAuthentication(token));

                    } catch (Exception e) {
                        log.error("❌ STOMP CONNECT 인증 실패: {}", e.getMessage(), e);
                        throw e;
                    }
                }

                return message;
            }
        };
    }
}
