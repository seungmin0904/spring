package com.example.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.example.jpa.entity.Board;
import java.util.List;

public interface BoardRepository
        extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board>, QuerydslPredicateExecutor<Board> {

    // where writer = 'user4'
    // findby컬럼명
    // List<Board> findByWriter(String writer);

    // List<Board> findByTitle(String title);

    // // writer like 'user4%'
    // List<Board> findByWriterStartingWith(String writer);

    // // '%user4'
    // List<Board> findByWriterEndingWith(String writer);

    // // '%user4%'
    // List<Board> findByWriterContaining(String writer);

    // // '%user%' or content like '%내용%' -> Containing : like
    // // '%user%' and content like '%내용%'
    // List<Board> findByWriterContainingOrContentContaining(String writer, String
    // content);

    // List<Board> findByWriterContainingAndContentContaining(String writer, String
    // content);

    // // id > 5 게시물 조회
    // List<Board> findByBnoGreaterThan(Long bno);

    // // orderby: bno > 0 order by bno desc
    // List<Board> findByBnoGreaterThanOrderByBnoDesc(Long bno);

    // // bno >= 5 and bno <= 10
    // // where bno between 5 and 10
    // List<Board> findByBnoBetween(Long start, Long end);

    // ----------------------------------------------------------------------------------
    // @Query : from 에 entity명 기준이어야 함 밑에 메서드는 마음대로 작성가능

    // @Query("select b from Board b where b.writer = ?1")
    @Query("select b from Board b where b.writer = :writer")

    List<Board> findByWriter(@Param("writer") String writer);

    @Query("select b from Board b where b.writer = ?1%")
    List<Board> findByWriterStartingWith(String writer);

    @Query("select b from Board b where b.writer like %?1%")
    List<Board> findByWriterContaining(String writer);

    // DB 테이블명으로 쓰는 sql 구문 형식
    // @Query(value ="쿼리문"nativeQuery = true)
    // @NativeQuery("쿼리문")
    @Query(value = "select * from Board b where b.bno < ?1", nativeQuery = true)
    List<Board> findByBnoGreaterThan(Long bno);

    @Query("select b.title,b.writer from Board b where b.title like %?1%")
    List<Object[]> findByTitle2(String title);

}
