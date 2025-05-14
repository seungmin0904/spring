package com.example.boardweb.board.mapper;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.example.boardweb.board.dto.ReplyRequestDTO;
import com.example.boardweb.board.dto.ReplyWebDTO;
import com.example.boardweb.board.entity.BoardWeb;
import com.example.boardweb.board.entity.ReplyWeb;

@Component
public class ReplyMapper {
    // Entity -> DTO 변환
    public ReplyWebDTO toDTO(ReplyWeb reply) {
        if (reply == null)
            return null;

        return ReplyWebDTO.builder()
                .rno(reply.getRno())
                .bno(reply.getBoardWeb().getBno())
                .text(reply.isDeleted() ? "삭제된 댓글입니다" : reply.getText())
                .replyer(reply.getReplyer())
                .username(reply.getUsername())
                .createdDate(reply.getCreatedDate())
                .moDateTime(reply.getUpdatedDate())
                .deleted(reply.isDeleted())
                .children(new ArrayList<>()) // 트리 조립 전에는 비워둠
                .build();
    }

    // DTO + 기타 정보 → Entity 변환
    public ReplyWeb toEntity(ReplyRequestDTO dto, BoardWeb board, ReplyWeb parent) {
        return ReplyWeb.builder()
                .boardWeb(board)
                .text(dto.getText())
                .replyer(dto.getReplyer())
                .username(dto.getUsername())
                .parent(parent)
                .build();
    }
}
