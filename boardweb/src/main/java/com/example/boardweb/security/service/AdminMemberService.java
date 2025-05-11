package com.example.boardweb.security.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMemberService {
    
    private final MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional
    public void suspendMember(String username, LocalDateTime until) {
        memberRepository.findById(username).ifPresent(member -> {
            member.setSuspended(true);
            member.setSuspendedUntil(until);
        });
    }

    @Transactional
    public void liftSuspension(String username) {
        memberRepository.findById(username).ifPresent(member -> {
            member.setSuspended(false);
            member.setSuspendedUntil(null);
        });
    }

    public Member getMembers(String username) {
    return memberRepository.findById(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
}
}
