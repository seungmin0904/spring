package com.example.boardweb.board.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.example.boardweb.board.dto.BoardRequestDTO;
import com.example.boardweb.board.dto.BoardResponseDTO;
import com.example.boardweb.board.dto.ReplyWebDTO;
import com.example.boardweb.board.entity.BoardWeb;
import com.example.boardweb.security.entity.Member;

public class BoardWebMapper {
    public static BoardWeb toEntity(BoardRequestDTO dto, Member member) {
        return BoardWeb.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .member(member)
                .build();
    }

    public static BoardResponseDTO toDTO(BoardWeb entity) {
        String writerName = entity.getMember().getName();
        String username = entity.getMember().getUsername();

        // 댓글 리스트 → ReplyWebDTO 리스트 변환
        List<ReplyWebDTO> replyDTOs = entity.getReplies().stream()
                .map(reply -> ReplyWebDTO.builder()
                        .rno(reply.getRno())
                        .bno(entity.getBno())
                        .text(reply.isDeleted() ? "삭제된 댓글입니다" : reply.getText())
                        .replyer(reply.getReplyer())
                        .username(reply.getUsername())
                        .createdDate(reply.getCreatedDate())
                        .deleted(reply.isDeleted())
                        .parentRno(reply.getParent() != null ? reply.getParent().getRno() : null)
                        .build())
                .collect(Collectors.toList());

        return BoardResponseDTO.builder()
                .bno(entity.getBno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(writerName)
                .username(username)
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .replies(replyDTOs)
                .build();
    }

}
