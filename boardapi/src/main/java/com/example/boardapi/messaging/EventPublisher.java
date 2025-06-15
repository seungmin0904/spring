package com.example.boardapi.messaging;

import com.example.boardapi.config.RabbitMQConfig;
import com.example.boardapi.dto.StatusChangeEvent;
import com.example.boardapi.enums.UserStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOnline(String username) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PRESENCE_EXCHANGE,
                "",
                new StatusChangeEvent(username, UserStatus.ONLINE));
    }

    public void publishOffline(String username) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PRESENCE_EXCHANGE,
                "",
                new StatusChangeEvent(username, UserStatus.OFFLINE));
    }
}