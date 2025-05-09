package com.example.book.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.book.dto.BookDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResultDTO;
import com.example.book.entity.Book;
import com.example.book.repository.BookRepository;

import javassist.runtime.Desc;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class Bookservice {
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public void insert(BookDTO dto) {
        // service 로직 에서는 주로 유지보수 및 코드의 빌드 구성 및 데이터 변경이 용이하도록 아래 주석 코드처럼 구성함
        // 필드가 많으면 번거롭고 실수 가능성 ↑
        // Book book = Book.builder()
        // .code(dto.getCode())
        // .author(dto.getAuthor())
        // .price(dto.getPrice())
        // .title(dto.getTitle())
        // .build();
        // bookRepository.save(book);

        // 필드명이 다르면 매핑 누락 또는 오류 발생 가능
        Book book = modelMapper.map(dto, Book.class);
        bookRepository.save(book);
    }

    public BookDTO read(Long code) {
        Book book = bookRepository.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("도서가 존재하지 않습니다: code=" + code));

        return modelMapper.map(book, BookDTO.class);
    }

    public PageResultDTO<BookDTO> readAll(PageRequestDTO pageRequestDTO) {

        Sort.Direction drection = Sort.Direction.fromString(pageRequestDTO.getSort());

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
                Sort.by(drection, "code").ascending());
        Page<Book> result = bookRepository
                .findAll(bookRepository.makePredicate(pageRequestDTO.getType(), pageRequestDTO.getKeyword()), pageable);

        List<BookDTO> books = result.get().map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
        Long totalCount = result.getTotalElements();

        return PageResultDTO.<BookDTO>withAll().dtoList(books).totalCount(totalCount).pageRequestDTO(pageRequestDTO)
                .build();

    }

    public void modify(BookDTO dto) {
        Book book = bookRepository.findById(dto.getCode())
                .orElseThrow(() -> new IllegalArgumentException("수정 할 도서가 존재하지 않습니다" + dto.getCode()));

        // book.setAuthor(dto.getAuthor());
        book.setPrice(dto.getPrice());
        // book.setTitle(dto.getTitle());

        bookRepository.save(book);

    }

    public void remove(Long code) {
        bookRepository.deleteById(code);
    }
}
