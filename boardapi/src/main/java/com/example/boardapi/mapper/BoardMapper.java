package com.example.boardapi.mapper;

import com.example.boardapi.dto.BoardRequestDTO;
import com.example.boardapi.dto.BoardResponseDTO;
import com.example.boardapi.entity.Board;

public class BoardMapper {

    public static Board toEntity(BoardRequestDTO boardRequestDTO) {
        return Board.builder()
                .title(boardRequestDTO.getTitle())
                .content(boardRequestDTO.getContent())
                .writer(boardRequestDTO.getWriter())
                .build();
    }

    public static BoardResponseDTO toDTO(Board entity) {
        return BoardResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .build();
    }
}
