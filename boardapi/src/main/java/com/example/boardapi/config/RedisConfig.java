package com.example.boardapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.boardapi.enums.RedisChannelConstants;
import com.example.boardapi.infra.RedisSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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

    @Bean(name = "EventRedisTemplate")
    public RedisTemplate<String, Object> redisTemplateForObject(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);

        // JavaTimeModule 포함된 ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 생성자에서 ObjectMapper를 직접 전달
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }

    @Bean
    public MessageListenerAdapter redisMessageListenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter redisMessageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(redisMessageListenerAdapter,
                new ChannelTopic(RedisChannelConstants.FRIEND_REQUEST_CHANNEL));
        container.addMessageListener(redisMessageListenerAdapter,
                new ChannelTopic(RedisChannelConstants.SERVER_MEMBER_CHANGE));
        container.addMessageListener(redisMessageListenerAdapter,
                new ChannelTopic(RedisChannelConstants.STATUS_CHANGE));
        container.addMessageListener(redisMessageListenerAdapter,
                new ChannelTopic(RedisChannelConstants.SERVER_CHANGE));
        container.addMessageListener(redisMessageListenerAdapter,
                new ChannelTopic(RedisChannelConstants.INVITE_CHANGE));

        return container;
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
