package com.example.boardapi.mapper;

import com.example.boardapi.dto.BoardRequestDTO;
import com.example.boardapi.dto.BoardResponseDTO;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;

public class BoardMapper {

    public static Board toEntity(BoardRequestDTO boardRequestDTO, Member member) {
        return Board.builder()
                .title(boardRequestDTO.getTitle())
                .content(boardRequestDTO.getContent())
                .member(member)
                .build();
    }

    public static BoardResponseDTO toDTO(Board entity) {
        return BoardResponseDTO.builder()
                .bno(entity.getBno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriterName())
                .build();
    }
}
