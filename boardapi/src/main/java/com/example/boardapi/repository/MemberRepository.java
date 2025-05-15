package com.example.boardapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardapi.entity.Member;

public interface MemberRepository extends JpaRepository<Member,Long>{
    
    
    // username(email)으로 회원 조회 (로그인, 이메일 중복 확인 등에 사용)
    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username); // 중복 체크용
}