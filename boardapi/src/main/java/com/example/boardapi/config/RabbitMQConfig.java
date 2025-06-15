package com.example.boardapi.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String PRESENCE_EXCHANGE = "presence.fanout";
    public static final String PRESENCE_QUEUE = "presence.queue";

    @Bean
    public FanoutExchange presenceExchange() {
        return new FanoutExchange(PRESENCE_EXCHANGE, true, false);
    }

    @Bean
    public Queue presenceQueue() {
        return QueueBuilder.durable(PRESENCE_QUEUE).build();
    }

    @Bean
    public Binding presenceBinding(Queue presenceQueue, FanoutExchange presenceExchange) {
        return BindingBuilder.bind(presenceQueue).to(presenceExchange);
    }
}
