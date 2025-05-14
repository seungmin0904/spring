package com.example.boardweb.board.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.boardweb.board.dto.ReplyRequestDTO;
import com.example.boardweb.board.dto.ReplyWebDTO;
import com.example.boardweb.board.service.ReplyWebService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("api/replies")
@RequiredArgsConstructor
public class ReplyRestController {

    private final ReplyWebService replyWebService;

    @PostMapping
    public ReplyWebDTO register(@RequestBody ReplyRequestDTO dto) {

        return replyWebService.create(dto);
    }

    @DeleteMapping("/{rno}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long rno) {
        replyWebService.delete(rno);
        return ResponseEntity.ok(Map.of("result", "success"));
    }

    @PutMapping("/{rno}")
    public ResponseEntity<ReplyWebDTO> modify(@PathVariable Long rno, @RequestBody ReplyRequestDTO dto) {
        dto.setRno(rno);
        ReplyWebDTO updated = replyWebService.modify(dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<ReplyWebDTO>> getReplies(@RequestParam Long bno) {
        List<ReplyWebDTO> replies = replyWebService.getReplies(bno);
        return ResponseEntity.ok(replies);
    }
}
