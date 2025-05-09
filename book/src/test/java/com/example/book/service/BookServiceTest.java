package com.example.book.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.book.dto.BookDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResultDTO;

@SpringBootTest
public class BookServiceTest {
    @Autowired
    private Bookservice bookservice;

    @Test
    public void listAllTest() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "ASC", "t", "Test");
        PageResultDTO<BookDTO> pageResultDTO = bookservice.readAll(pageRequestDTO);

        System.out.println(pageResultDTO.getDtoList());

        System.out.println("내용");
        System.out.println(pageResultDTO.getDtoList());
        System.out.println("페이지 나누기 정보");
        System.out.println("Total page" + pageResultDTO.getTotalPage());
        System.out.println("PageNumList" + pageResultDTO.getPageNumList());
        System.out.println("next " + pageResultDTO.isNext());
        System.out.println("prev " + pageResultDTO.isPrev());
    }
}
