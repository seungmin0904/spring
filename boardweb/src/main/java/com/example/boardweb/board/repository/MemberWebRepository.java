package com.example.boardweb.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardweb.board.entity.MemberWeb;


public interface MemberWebRepository extends JpaRepository<MemberWeb, String> {

}
