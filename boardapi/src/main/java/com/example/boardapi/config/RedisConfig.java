package com.example.boardapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    /*
     * @Bean
     * public RedisMessageListenerContainer keyExpirationListenerContainer(
     * RedisConnectionFactory cf,
     * RedisKeyExpirationListener listener) {
     * RedisMessageListenerContainer container = new
     * RedisMessageListenerContainer();
     * container.setConnectionFactory(cf);
     * container.addMessageListener(listener, new
     * PatternTopic("__keyevent@*__:expired"));
     * return container;
     * }
     */
}
