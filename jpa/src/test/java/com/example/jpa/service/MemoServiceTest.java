package com.example.jpa.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.jpa.dto.MemoDTO;


@SpringBootTest
public class MemoServiceTest {
    @Autowired
    private MemoService memoService;

    @Test
    public void getListTest() {
        List<MemoDTO> list = memoService.getList();
        list.forEach(dto -> System.out.println(dto));
    }

    @Test
    public void getRowTest() {
        MemoDTO dto = memoService.getRow(2L);
        System.out.println(dto);
    }

    @Test
    public void memoUpdateTest() {
        MemoDTO dto = MemoDTO.builder().mno(2L).memoText("수정 메세지").build();
        Long mno = memoService.memoUpdate(dto);
        System.out.println("수정된 mno " + mno);
    }

    @Test
    public void memoDeleteTest() {
        memoService.memoDelete(5L);
    }

    @Test
    public void memoCreateTest() {
        MemoDTO dto = MemoDTO.builder().memoText("memo 추가").build();
        System.out.println("추가된 mno " + memoService.memoCreate(dto));
    }
}
