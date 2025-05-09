package com.example.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mart.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
