package com.example.boardapi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import com.example.boardapi.dto.BoardRequestDTO;
import com.example.boardapi.dto.BoardResponseDTO;
import com.example.boardapi.dto.BoardWithRepliesDTO;
import com.example.boardapi.dto.PageRequestDTO;
import com.example.boardapi.dto.PageResultDTO;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;
import com.example.boardapi.mapper.BoardMapper;
import com.example.boardapi.security.dto.MemberSecurityDTO;
import com.example.boardapi.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // 게시글 등록
    @PostMapping
    public ResponseEntity<BoardResponseDTO> register(@RequestBody BoardRequestDTO dto,
            @AuthenticationPrincipal MemberSecurityDTO memberDTO) {
                log.info("🧑 로그인 사용자 = {}", memberDTO.getUsername()); // null이면 인증 실패 상태임
        Board saved = boardService.register(dto, memberDTO.getUsername());
        return ResponseEntity.ok(BoardMapper.toDTO(saved));
    }

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<PageResultDTO<BoardResponseDTO>> getAll(PageRequestDTO pageRequestDTO) {
    return ResponseEntity.ok(boardService.getAll(pageRequestDTO));
    }

    // 게시글 단건 조회
    @GetMapping("/{bno}")
    public ResponseEntity<BoardResponseDTO> get(@PathVariable Long bno) {
        return ResponseEntity.ok(boardService.get(bno));
    }

    // 게시글 수정
    @PutMapping("/{bno}")
    public ResponseEntity<Void> modify(@PathVariable Long bno,
            @RequestBody BoardRequestDTO dto,
            @AuthenticationPrincipal MemberSecurityDTO memberDTO) {
                
        boardService.modify(bno, dto, memberDTO.getUsername());
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제
    @DeleteMapping("/{bno}")
    public ResponseEntity<Void> delete(@PathVariable Long bno,
            @AuthenticationPrincipal Member member) {
        boardService.delete(bno, member);
        return ResponseEntity.ok().build();
    }

    // 게시글 1 댓글 전체 불러오기  

@GetMapping("/{bno}/full")
public ResponseEntity<BoardWithRepliesDTO> getFull(@PathVariable Long bno) {
    return ResponseEntity.ok(boardService.getBoardWithReplies(bno));
}
}
