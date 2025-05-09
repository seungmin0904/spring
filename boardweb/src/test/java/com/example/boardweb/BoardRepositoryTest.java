package com.example.boardweb;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.boardweb.board.entity.BoardWeb;
import com.example.boardweb.board.entity.MemberWeb;
import com.example.boardweb.board.entity.ReplyWeb;
import com.example.boardweb.board.repository.BoardWebRepository;
import com.example.boardweb.board.repository.MemberWebRepository;
import com.example.boardweb.board.repository.ReplyWebRepository;

import jakarta.transaction.Transactional;
@EnableJpaAuditing
@SpringBootTest
public class BoardRepositoryTest {
    
    @Autowired
    private MemberWebRepository memberWebRepository;
    @Autowired
    private BoardWebRepository boardWebRepository;

    @Autowired
    private ReplyWebRepository replyWebRepository;
    @Test
    public void insertTest() {
         IntStream.rangeClosed(1, 10).forEach(i -> {
            MemberWeb member = MemberWeb.builder()
            .password("1111")
            .name("user" + i)
            .email("user" + i + "@gmail.com")
            .build();
            memberWebRepository.save(member);
        });

        // Test code for insert operation
    }

    @Test
    public void BoardTest() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            int no = (int) (Math.random() * 10) + 1;
            MemberWeb member = MemberWeb.builder().email("user" + no + "@gmail.com").build();
           BoardWeb boardWeb = BoardWeb.builder()
           .title("Title" + i)
           .content("Content" + i)
           .memberWeb(member)
           .build();
          boardWebRepository.save(boardWeb);
       });

       // Test code for insert operation
   }

   @Test
   public void replyerTest() {
    IntStream.rangeClosed(1, 100).forEach(i -> {
        long no = (int) (Math.random() * 100) + 1;
        BoardWeb boardWeb = BoardWeb.builder().bno(no).build();
        ReplyWeb replyWeb = ReplyWeb.builder()
        .text("Reply" + i)
        .replyer("guest"+i)
        .boardWeb(boardWeb)
        .build();
      replyWebRepository.save(replyWeb);
   });
}

   @Transactional
   @Test
   public void readBoardTest() {
    BoardWeb boardWeb = boardWebRepository.findById(2L).get();
    System.out.println(boardWeb.getMemberWeb());
     
   }

   @Transactional
   @Test
   public void readBoardTest3() {
    BoardWeb boardWeb = boardWebRepository.findById(99L).get();
    System.out.println(boardWeb.getMemberWeb());
    
     
   }

   @Test
   public void readReplyTest() {
    ReplyWeb replyWeb = replyWebRepository.findById(2L).get();
    System.out.println(replyWeb);
    System.out.println(replyWeb.getBoardWeb());
     
   }

   
   @Test
   public void listTest(){
  Page<Object[]> result = boardWebRepository.list();
  for (Object[] objects : result) {
   BoardWeb boardWeb = (BoardWeb) objects[0];
   MemberWeb memberWeb= (MemberWeb) objects[1];
    Long count = (Long) objects[2];
    System.out.println(boardWeb);
    System.out.println(memberWeb);
    System.out.println(count);
  }

   }

}
