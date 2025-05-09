package com.example.jpa.repository;

import java.util.List;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.jpa.entity.Board;
import com.example.jpa.entity.QBoard;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@SpringBootTest
public class BoardRepositoryTest {
    @Autowired
    private BoardRepository boardRepository;
    private EntityManager em;

    @Test
    public void queryMethodTest() {
        // System.out.println(boardRepository.findByWriter("user4"));
        // System.out.println(boardRepository.findByTitle("title4"));
        // System.out.println(boardRepository.findByWriterContaining("user"));
        // System.out.println(boardRepository.findByWriterEndingWith("user"));
        // System.out.println(boardRepository.findByWriterStartingWith("user"));

        // System.out.println(boardRepository.findByWriterContainingOrContentContaining("5",
        // "9"));
        // System.out.println(boardRepository.findByWriterContainingAndContentContaining("5",
        // "9"));

        // System.out.println(boardRepository.findByBnoGreaterThan(5L));
        // System.out.println(boardRepository.findByBnoGreaterThanOrderByBnoDesc(0L));
        // System.out.println(boardRepository.findByBnoBetween(5L, 10L));

        // List<Object[]> result = boardRepository.findByTitle2("title");
        // for (Object[] objects : result) {
        // String title = (String) objects[0];
        // String writer = (String) objects[1];
        // System.out.println("title : " + title + "writer : " + writer);
        // System.out.println("================================");

        // }
        System.out.println();
    }

    @Test
    public void insertTest() {
        LongStream.rangeClosed(1, 100).forEach(i -> {
            Board board = Board.builder().content("content" + i).writer("user" + i).title("title" + i).build();
            boardRepository.save(board);
        });
    }

    @Test
    public void updateTest() {
        Board board = boardRepository.findById(3L).get();
        board.setContent("content update");
        board.setTitle("title update");
        board.setWriter("writer update");
        boardRepository.save(board);
    }

    @Test
    public void readTest() {
        Board board = boardRepository.findById(5L).get();
        System.out.println(board);
    }

    @Test
    public void listTest() {

        // pageNumber 0 = 1page, pageSize 10 = 10개씩
        Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());
        // boardRepository.findAll().forEach(board -> System.out.println(board));
        boardRepository.findAll(pageable).forEach(board -> System.out.println(board));
    }

    @Test
    public void deleteTest() {
        boardRepository.deleteById(10L);
    }

    @Test
    public void DslTest() {
        QBoard board = QBoard.board;

        Iterable<Board> boards = boardRepository.findAll(board.title.contains("title1")
                .and(board.bno.gt(0L)), Sort.by("bno").descending());
        System.out.println(boards);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        Page<Board> page = boardRepository.findAll(board.bno.gt(0L), pageable);
        System.out.println("page size" + page.getSize());
        System.out.println("page Totalpage" + page.getTotalPages());
        System.out.println("page TotalElements" + page.getTotalElements());
        System.out.println("page Content" + page.getContent());
    }

    public List<Board> testQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QBoard qBoard = QBoard.board;
        System.out.println(qBoard);
        // return queryFactory.selectFrom(qBoard).fetch();
        return null;
    }
}
