package com.example.boardapi.listener;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.boardapi.messaging.DmRestoreEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DmRestoreListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRestore(DmRestoreEvent event) {
        String username = event.getUsername();
        Long roomId = event.getRoomId();

        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/dm-restore",
                Map.of("roomId", roomId, "status", "RESTORE"));

        log.info("📡 [AFTER_COMMIT] DM 복구 WebSocket 전송 → {}", username);
    }
}
