package com.example.boardweb.board.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.boardweb.board.dto.PageRequestDTO;

public interface SearchBoardRepository {
    Page<Object[]> list(PageRequestDTO requestDTO, Pageable pageable);

    // 페이징이 완료된 상태에서 전체 리스트용
    default Page<Object[]> list(Pageable pageable) {
        return list(PageRequestDTO.builder().build(), pageable);
    }

    // 기본값으로 Pageable.unpaged()를 사용하여 페이지 매김을 하지 않는 메서드
    default Page<Object[]> list() {
        return list(PageRequestDTO.builder().build(), Pageable.unpaged());
    }
}
