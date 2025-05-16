package com.example.boardapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.boardapi.dto.BoardRequestDTO;
import com.example.boardapi.dto.BoardResponseDTO;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;
import com.example.boardapi.mapper.BoardMapper;
import com.example.boardapi.repository.BoardRepository;
import com.example.boardapi.repository.MemberRepository;
import com.example.boardapi.security.service.SecurityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final SecurityService securityService;

    // 게시글 등록
    public Board register(BoardRequestDTO dto, Member member) {
        Board board = BoardMapper.toEntity(dto, member);
        boardRepository.save(board);
        return board;
    }

    // 전체 게시글 조회
    public List<BoardResponseDTO> getAll() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(BoardMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 게시글 단건 조회
    public BoardResponseDTO get(Long bno) {
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));
        return BoardMapper.toDTO(board);
    }

    // 게시글 수정
    public void modify(Long bno, BoardRequestDTO dto, Member currentUser) {
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("수정할 게시글이 없습니다"));

        securityService.checkBoardOwnership(board, currentUser);
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());

        boardRepository.save(board);
    }

    // 게시글 삭제
    public void delete(Long bno, Member currentUser) {
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 게시글이 없습니다"));

        securityService.checkBoardOwnership(board, currentUser); // 권한 검증
        boardRepository.deleteById(bno);
    }
}
