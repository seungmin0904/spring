package com.example.boardweb.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardweb.board.entity.BoardWeb;
import com.example.boardweb.board.repository.search.SearchBoardRepository;



public interface BoardWebRepository extends JpaRepository<BoardWeb, Long>, SearchBoardRepository {

}
