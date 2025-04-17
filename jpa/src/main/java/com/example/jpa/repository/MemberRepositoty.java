package com.example.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jpa.entity.Member;

public interface MemberRepositoty extends JpaRepository<Member, Long> {

}
