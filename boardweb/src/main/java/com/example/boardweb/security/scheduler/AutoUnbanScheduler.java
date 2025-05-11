package com.example.boardweb.security.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.repository.MemberRepository;
import com.example.boardweb.security.service.SecurityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoUnbanScheduler {
    
     private final MemberRepository memberRepository;
     private final SecurityService securityService;

     
     // 매 30분마다 정지 해제 시점이 지난 사용자들을 해제 처리
    @Scheduled(fixedDelay = 1000 * 60 * 30) 
    // @Scheduled(fixedDelay = 5000)
    public void checkAndLiftExpiredSuspensions() {
        List<Member> expiredList = memberRepository
                .findAllBySuspendedTrueAndSuspendedUntilBefore(LocalDateTime.now());

        for (Member member : expiredList) {
            log.info("자동 해제 대상: {}", member.getUsername());

            // 자동 해제 처리 (false → 수동 해제 아님)
            securityService.liftSuspension(member.getUsername(), false);
        }
    }



}
