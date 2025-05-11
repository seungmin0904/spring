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
import com.example.boardweb.board.entity.ReplyWeb;
import com.example.boardweb.board.repository.BoardWebRepository;
import com.example.boardweb.board.repository.ReplyWebRepository;

import jakarta.transaction.Transactional;
@EnableJpaAuditing
@SpringBootTest
public class BoardRepositoryTest {
    
    
    @Autowired
    private BoardWebRepository boardWebRepository;

    @Autowired
    private ReplyWebRepository replyWebRepository;
   


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

   

   

   @Test
   public void readReplyTest() {
    ReplyWeb replyWeb = replyWebRepository.findById(2L).get();
    System.out.println(replyWeb);
    System.out.println(replyWeb.getBoardWeb());
     
   }

   


}
