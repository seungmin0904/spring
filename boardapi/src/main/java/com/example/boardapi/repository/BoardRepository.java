package com.example.boardapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardapi.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>,BoardRepositoryCustom {

}
