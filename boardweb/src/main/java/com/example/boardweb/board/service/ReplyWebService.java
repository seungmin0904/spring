package com.example.boardweb.board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.boardweb.board.dto.ReplyRequestDTO;
import com.example.boardweb.board.dto.ReplyWebDTO;
import com.example.boardweb.board.entity.BoardWeb;
import com.example.boardweb.board.entity.ReplyWeb;
import com.example.boardweb.board.repository.BoardWebRepository;
import com.example.boardweb.board.repository.ReplyWebRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyWebService {
    private final ReplyWebRepository replyRepo;
    private final BoardWebRepository boardRepo;

    
     /** 댓글/답글 등록 **/
     @Transactional
     public void create(ReplyRequestDTO dto) {
         BoardWeb board = boardRepo.findById(dto.getBno())
             .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + dto.getBno()));
 
         ReplyWeb parent = null;
         if (dto.getParentRno() != null && dto.getParentRno() > 0) {
             parent = replyRepo.findById(dto.getParentRno())
                 .orElseThrow(() -> new IllegalArgumentException("부모댓글 없음: " + dto.getParentRno()));
         }
 
         ReplyWeb reply = ReplyWeb.builder()
             .boardWeb(board)
             .replyer(dto.getReplyer())
             .text(dto.getText())
             .parent(parent)
             .build();
 
         replyRepo.save(reply);
     }
 
     /** 댓글/답글 수정 **/
     @Transactional
     public void modify(ReplyRequestDTO dto) {
         ReplyWeb reply = replyRepo.findById(dto.getRno())
             .orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + dto.getRno()));
         reply.setText(dto.getText());
         reply.setReplyer(dto.getReplyer());
         // 변경 감지로 트랜잭션 커밋 시 자동 반영
     }
 
     /** 댓글/답글 삭제 **/
     @Transactional
     public void delete(Long rno) {
         ReplyWeb reply = replyRepo.findById(rno)
             .orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + rno));
         reply.setDeleted(true);
     }
     
     public ReplyWebDTO readOne(Long rno) {
        ReplyWeb reply = replyRepo.findById(rno)
            .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + rno));

        return ReplyWebDTO.builder()
                .bno(reply.getBoardWeb().getBno())
                .rno(reply.getRno())
                .text(reply.getText())
                .replyer(reply.getReplyer())
                .createdDate(reply.getCreatedDate())
                .deleted(reply.isDeleted())
                .moDateTime(reply.getUpdatedDate())
                .build();
    }
 }