package com.example.boardapi.mapper;
import com.example.boardapi.dto.ReplyDTO;
import com.example.boardapi.dto.ReplyRequestDTO;
import com.example.boardapi.entity.*;

import org.springframework.stereotype.Component;

@Component
public class ReplyMapper {

    public ReplyDTO toDTO(Reply entity) {
        return ReplyDTO.builder()
                .rno(entity.getRno())
                .bno(entity.getBoard().getBno())
                .text(entity.getText())
                .replyer(entity.getMember().getName())
                .username(entity.getMember().getUsername())
                .createdDate(entity.getCreatedDate())
                .build();
    }

    public Reply toEntity(ReplyRequestDTO dto, Board board, Member member, Reply parent) {
        return Reply.builder()
                .text(dto.getText())
                .board(board)
                .member(member)
                .parent(parent)
                .build();
    }
}
