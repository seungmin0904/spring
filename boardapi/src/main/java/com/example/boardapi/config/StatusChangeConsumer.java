package com.example.boardapi.config;

import com.example.boardapi.dto.StatusChangeEvent;
import com.example.boardapi.service.FriendService;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatusChangeConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final FriendService friendService;

    @RabbitListener(queues = "status.queue")
    public void handleStatusChange(StatusChangeEvent event) {
        List<String> friendUsernames = friendService.getFriendUsernames(event.getUsername());
        for (String friend : friendUsernames) {
            messagingTemplate.convertAndSendToUser(friend, "/queue/status", event);
        }
    }
}
