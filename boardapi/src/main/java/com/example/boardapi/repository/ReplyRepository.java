package com.example.boardapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardapi.entity.reply;

public interface ReplyRepository extends JpaRepository<reply, Long> {

}
