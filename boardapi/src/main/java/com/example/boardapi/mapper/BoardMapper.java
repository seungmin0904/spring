package com.example.boardapi.mapper;

import org.springframework.stereotype.Component;

import com.example.boardapi.dto.BoardRequestDTO;
import com.example.boardapi.dto.BoardResponseDTO;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BoardMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Board toEntity(BoardRequestDTO boardRequestDTO, Member member) {
        String attachmentsJson = null;
        
        try {
            attachmentsJson = boardRequestDTO.getAttachments() != null
                    ? objectMapper.writeValueAsString(boardRequestDTO.getAttachments())
                    : null;
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // 또는 Logger로 처리해도 됨
        }

        String content = boardRequestDTO.getContent();
        String imageUrl = boardRequestDTO.getImageUrl();

        // 기존 content에 <img> 태그가 이미 있을 경우 제거
        content = content.replaceAll("(?i)<img[^>]*><br\\s*/?>\\s*", "").trim();

        // 이미지가 있으면 content 앞에 이미지 태그 삽입
        if (imageUrl != null && !imageUrl.isBlank()) {
        // 개발 환경에서는 localhost, 운영환경에서는 설정값으로 바꾸는 게 좋음
        String fullImageUrl = "http://localhost:8080" + imageUrl;
        // 본문에 절대경로 이미지 포함
        content = "<img src=\"" + fullImageUrl + "\" alt=\"본문 이미지\" />" + content;
    }
        return Board.builder()
                .title(boardRequestDTO.getTitle())
                .content(content)
                .imageUrl(imageUrl) // 썸네일 이미지
                .attachmentsJson(attachmentsJson) // 첨부파일 목록 (JSON 저장)
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
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
