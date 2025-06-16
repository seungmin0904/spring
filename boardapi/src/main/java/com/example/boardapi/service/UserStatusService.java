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

    private final RedisTemplate<String, String> redisTemplate;
    private final EventPublisher eventPublisher;
    private final SimpMessagingTemplate messagingTemplate;
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    public void markOnline(String username) {
        log.info("markOnline: {}", username);

        // ✅ online_users 세트에 기록만 남기기
        redisTemplate.opsForSet().add("online_users", username);

        // ✅ RabbitMQ + WebSocket로 상태 브로드캐스트
        eventPublisher.publishOnline(username);
        messagingTemplate.convertAndSend("/topic/online-users",
                new StatusChangeEvent(username, UserStatus.ONLINE));
    }

    public void markOffline(String username) {
        log.info("markOffline: {}", username);

        // ✅ online_users 세트에서 제거
        redisTemplate.opsForSet().remove("online_users", username);

        // ✅ RabbitMQ + WebSocket로 상태 브로드캐스트
        eventPublisher.publishOffline(username);
        messagingTemplate.convertAndSend("/topic/online-users",
                new StatusChangeEvent(username, UserStatus.OFFLINE));
    }

    public List<String> getOnlineFriendUsernames(String myUsername) {
        Member me = memberRepository.findByUsername(myUsername)
                .orElseThrow(() -> new UsernameNotFoundException(myUsername));

        List<Friend> accepted = friendRepository.findAcceptedFriends(FriendStatus.ACCEPTED, me.getMno());

        List<String> allFriends = accepted.stream()
                .map(f -> f.getMemberA().getMno().equals(me.getMno()) ? f.getMemberB().getUsername()
                        : f.getMemberA().getUsername())
                .collect(Collectors.toList());

        Set<String> online = redisTemplate.opsForSet().members("online_users");
        return allFriends.stream().filter(online::contains).collect(Collectors.toList());
    }

    public List<String> getFriendUsernames(String myUsername) {
        Member me = memberRepository.findByUsername(myUsername)
                .orElseThrow(() -> new UsernameNotFoundException(myUsername));

        List<Friend> accepted = friendRepository.findAcceptedFriends(FriendStatus.ACCEPTED, me.getMno());

        return accepted.stream()
                .map(f -> f.getMemberA().getMno().equals(me.getMno()) ? f.getMemberB().getUsername()
                        : f.getMemberA().getUsername())
                .collect(Collectors.toList());
    }
}