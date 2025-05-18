package com.example.boardapi.mapper;

import org.springframework.stereotype.Component;

import com.example.boardapi.dto.BoardRequestDTO;
import com.example.boardapi.dto.BoardResponseDTO;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;

@Component
public class BoardMapper {

    public static Board toEntity(BoardRequestDTO boardRequestDTO, Member member) {
        return Board.builder()
                .title(boardRequestDTO.getTitle())
                .content(boardRequestDTO.getContent())
                .member(member)
                .build();
    }

    public static BoardResponseDTO toDTO(Board entity) {
        Member member = entity.getMember();

    String writerName = (member != null && member.getMno() != null)
            ? member.getName()
            : "알 수 없음";

        return BoardResponseDTO.builder()
                .bno(entity.getBno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writerName(writerName)
                .createdDate(entity.getCreatedDate().toString())
                .modifiedDate(entity.getUpdatedDate().toString())
                .build();
    }
}
