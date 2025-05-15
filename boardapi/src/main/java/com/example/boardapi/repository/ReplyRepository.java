package com.example.boardapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply,Long>{
     List<Reply> findByBoardOrderByCreatedDateAsc(Board board);
}
