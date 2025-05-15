package com.example.boardapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.boardapi.dto.BoardRequestDTO;
import com.example.boardapi.dto.BoardResponseDTO;
import com.example.boardapi.entity.Board;
import com.example.boardapi.mapper.BoardMapper;
import com.example.boardapi.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardResponseDTO register(BoardRequestDTO dto) {
        Board board = boardRepository.save(BoardMapper.toEntity(dto));

        return BoardMapper.toDTO(board);
    }

    public List<BoardResponseDTO> getList() {
        return boardRepository.findAll()
                .stream()
                .map(BoardMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BoardResponseDTO get(Long id) {
        return boardRepository.findById(id)
                .map(BoardMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }
}
