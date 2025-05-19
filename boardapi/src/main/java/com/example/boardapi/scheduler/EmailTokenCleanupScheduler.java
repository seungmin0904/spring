package com.example.boardapi.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.boardapi.repository.EmailVerificationTokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailTokenCleanupScheduler {

    private final EmailVerificationTokenRepository tokenRepository;

    @Scheduled(fixedDelay = 300000) // 3분마다 실행
    @Transactional
    public void deleteExpiredTokens() {
        System.out.println("🧹 토큰 삭제 스케줄러 실행됨: " + LocalDateTime.now());
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
