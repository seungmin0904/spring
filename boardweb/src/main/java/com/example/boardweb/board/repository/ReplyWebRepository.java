package com.example.boardweb.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.boardweb.board.entity.BoardWeb;
import com.example.boardweb.board.entity.ReplyWeb;



public interface ReplyWebRepository extends JpaRepository<ReplyWeb, Long> {

   long countByBoardWeb(BoardWeb boardWeb); // boardWeb에 대한 댓글 수를 세는 메서드
   void deleteByBoardWeb(BoardWeb boardWeb); // boardWeb에 대한 댓글을 삭제하는 메서드 
   List<ReplyWeb>findByBoardWebOrderByCreatedDateAsc(BoardWeb boardWeb); // boardWeb에 대한 댓글을 조회하는 메서드
   List<ReplyWeb> findByParent(ReplyWeb parent); // 대댓글을 조회하는 메서드

   @Query("""
    SELECT r 
    FROM ReplyWeb r
    LEFT JOIN FETCH r.parent p
    WHERE r.boardWeb = :boardWeb
    ORDER BY r.createdDate ASC
    """)
   List<ReplyWeb> parentReplyWebs(BoardWeb boardWeb); // 부모 댓글을 조회하는 메서드
}
