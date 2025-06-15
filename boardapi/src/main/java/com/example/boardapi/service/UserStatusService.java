package com.example.boardapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.boardapi.dto.StatusChangeEvent;
import com.example.boardapi.entity.Friend;
import com.example.boardapi.entity.FriendStatus;
import com.example.boardapi.entity.Member;
import com.example.boardapi.enums.UserStatus;
import com.example.boardapi.messaging.EventPublisher;
import com.example.boardapi.repository.FriendRepository;
import com.example.boardapi.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatusService {

    private static final long TTL_SECONDS = 30;

    private final RedisTemplate<String, String> redisTemplate;
    private final EventPublisher eventPublisher;
    private final SimpMessagingTemplate messagingTemplate;
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    public void markOnline(String username) {
        log.info("markOnline: {}", username);
        // 1) Redis에 TTL 기반 키 저장
        String key = "user:status:" + username;
        redisTemplate.opsForValue().set(key, "ONLINE", TTL_SECONDS, TimeUnit.SECONDS);

        // 2) RabbitMQ로 ONLINE 이벤트 발행
        eventPublisher.publishOnline(username);

        // 3) 즉시 WebSocket으로 브로드캐스트
        messagingTemplate.convertAndSend("/topic/online-users",
                new StatusChangeEvent(username, UserStatus.ONLINE));
    }

    public void markOffline(String username) {
        log.info("markOffline: {}", username);
        // 1) Redis 키 삭제 (또는 만료)
        String key = "user:status:" + username;
        redisTemplate.delete(key);

        // 2) RabbitMQ로 OFFLINE 이벤트 발행
        eventPublisher.publishOffline(username);

        // 3) 즉시 WebSocket으로 브로드캐스트
        messagingTemplate.convertAndSend("/topic/online-users",
                new StatusChangeEvent(username, UserStatus.OFFLINE));
    }

    public List<String> getOnlineFriendUsernames(String myUsername) {
        // 1) 내 Member 객체 조회
        Member me = memberRepository.findByUsername(myUsername)
                .orElseThrow(() -> new UsernameNotFoundException(myUsername));

        // 2) 수락된 친구 목록 꺼내기
        List<Friend> accepted = friendRepository.findAcceptedFriends(FriendStatus.ACCEPTED, me.getMno());

        // 3) 상대방 username만 추출
        List<String> allFriends = accepted.stream()
                .map(f -> {
                    if (f.getMemberA().getMno().equals(me.getMno()))
                        return f.getMemberB().getUsername();
                    else
                        return f.getMemberA().getUsername();
                })
                .collect(Collectors.toList());

        // 4) Redis 세트와 교집합
        Set<String> online = redisTemplate.opsForSet().members("online_users");
        return allFriends.stream()
                .filter(online::contains)
                .collect(Collectors.toList());
    }

}
