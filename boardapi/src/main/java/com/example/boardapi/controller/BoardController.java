package com.example.boardapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.boardapi.dto.BoardRequestDTO;
import com.example.boardapi.dto.BoardResponseDTO;
import com.example.boardapi.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // 게시글 등록
    @PostMapping
    public ResponseEntity<Void> register(@RequestBody BoardRequestDTO dto) {
        boardService.register(dto);
        return ResponseEntity.ok().build();
    }

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<List<BoardResponseDTO>> getAll() {
        return ResponseEntity.ok(boardService.getAll());
    }

    // 게시글 단건 조회
    @GetMapping("/{bno}")
    public ResponseEntity<BoardResponseDTO> get(@PathVariable Long bno) {
        return ResponseEntity.ok(boardService.get(bno));
    }

    // 게시글 수정
    @PutMapping("/{bno}")
    public ResponseEntity<Void> modify(@PathVariable Long bno,
                                       @RequestBody BoardRequestDTO dto) {
        boardService.modify(bno, dto);
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제
    @DeleteMapping("/{bno}")
    public ResponseEntity<Void> delete(@PathVariable Long bno) {
        boardService.delete(bno);
        return ResponseEntity.ok().build();
    }
}
