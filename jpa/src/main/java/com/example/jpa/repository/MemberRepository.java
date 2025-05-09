package com.example.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // DB에 직접 접근
    // JpaRepository를 상속받아 sql없이 DB 처리 가능
}
