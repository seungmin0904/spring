package com.example.boardapi.controller;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.example.boardapi.entity.Board;
import com.example.boardapi.repository.BoardRepository;
import com.example.boardapi.util.HtmlUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ThumbnailController {
    private final BoardRepository boardRepository;

    @GetMapping("/api/thumbnail/{bno}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable Long bno) {
        // 게시글 가져오기
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // content 에서 첫 번째 <img src="..."> 추출
        String url = HtmlUtils.extractFirstImageUrl(board.getContent());
if (url == null) return ResponseEntity.notFound().build();

String imagePath;
int idx = url.indexOf("/uploads/");
if (idx != -1) {
    imagePath = url.substring(idx);
} else {
    return ResponseEntity.notFound().build();
}
File file = new File("." + imagePath);
System.out.println("현재 실행 디렉토리: " + System.getProperty("user.dir"));
System.out.println("이미지 파일 경로: " + file.getAbsolutePath());
System.out.println("파일 존재?: " + file.exists());

if (!file.exists()) return ResponseEntity.notFound().build();
Resource resource = new FileSystemResource(file);
        // 이미지 타입 자동 감지 (png, jpg, etc)
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
