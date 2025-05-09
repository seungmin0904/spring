package com.example.jpa.service;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.example.jpa.dto.MemoDTO;

// 메서드 정의 

public interface MemoService {
    List<MemoDTO> getList();
    MemoDTO getRow(Long mno);
    Long memoUpdate(MemoDTO dto);
    void memoDelete(Long mno);
    Long memoCreate(MemoDTO dto);
    Page<MemoDTO> getListPage(Pageable pageable);

    // 전체 구조 흐름 
    // [브라우저] 
    //    ↓ HTTP 요청
    // [MemoController]
    //    ↓ memoService.getListPage()
    // [MemoServiceImpl]
    //    ↓ memoRepository.findAll(pageable)
    // [DB에서 메모 가져오기]
    //    ↑ 결과 (List<Memo>)
    // [MemoServiceImpl]
    //    ↓ entity → dto 변환
    //    ↑ 결과 (Page<MemoDTO>)
    // [MemoController]
    //    ↓ model.addAttribute("list", ...)
    // [Thymeleaf 템플릿에서 화면 렌더링]
}
