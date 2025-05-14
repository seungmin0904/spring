package com.example.boardweb.board.api;

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

import com.example.boardweb.board.dto.BoardRequestDTO;
import com.example.boardweb.board.dto.BoardResponseDTO;
import com.example.boardweb.board.dto.BoardWebDTO;
import com.example.boardweb.board.dto.PageRequestDTO;
import com.example.boardweb.board.dto.PageResultDTO;
import com.example.boardweb.board.service.BoardWebService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardWebRestController {

    private final BoardWebService boardWebService; // 서비스만 주입

    @PostMapping
    public ResponseEntity<String> register(@RequestBody @Valid BoardRequestDTO dto) {
        Long bno = boardWebService.create(dto); // 위임
        return ResponseEntity.ok("등록 완료" + bno);
    }

    @GetMapping
    public ResponseEntity<PageResultDTO<BoardWebDTO>> getList(PageRequestDTO pageRequestDTO) {
        PageResultDTO<BoardWebDTO> result = boardWebService.getList(pageRequestDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{bno}")
    public ResponseEntity<BoardWebDTO> getOne(@PathVariable("bno") Long bno) {
        BoardWebDTO dto = boardWebService.read(bno);
        return ResponseEntity.ok(dto);
    }

    // 게시글 수정
    @PutMapping("/{bno}")
    public ResponseEntity<String> modify(@PathVariable("bno") Long bno,
            @RequestBody @Valid BoardWebDTO dto) {
        dto.setBno(bno); // URL에서 받은 bno를 DTO에 주입
        boardWebService.modify(dto);
        return ResponseEntity.ok("수정 완료");
    }

    // 게시글 삭제
    @DeleteMapping("/{bno}")
    public ResponseEntity<String> delete(@PathVariable("bno") Long bno) {
        boardWebService.delete(bno);
        return ResponseEntity.ok("삭제 완료");
    }
}