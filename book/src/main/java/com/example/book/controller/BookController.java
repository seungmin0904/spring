package com.example.book.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.book.dto.BookDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResultDTO;
import com.example.book.entity.Book;
import com.example.book.repository.BookRepository;
import com.example.book.service.Bookservice;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/book")
@Log4j2
@Controller
@RequiredArgsConstructor
public class BookController {

    private final Bookservice bookservice;

    @GetMapping("/create")
    public void getCreate(@ModelAttribute("book") BookDTO dto, PageRequestDTO pageRequestDTO) {
        log.info("도서 작성 폼 ");
    }

    @PostMapping("/create")
    public String postCreate(@ModelAttribute("book") @Valid BookDTO dto, BindingResult result,
            RedirectAttributes rttr) {
        log.info("도서 작성  ");
        if (result.hasErrors()) {
            return "/book/create";
        }
        bookservice.insert(dto);
        // rttr.addAttribute(dto.getTitle());
        // rttr.addAttribute(dto.getAuthor());
        // rttr.addAttribute(dto.getPrice());

        return "redirect:/book/list";
    }

    @GetMapping("/list")
    public void getList(PageRequestDTO pageRequestDTO, Model model) {
        log.info("book list 요청", pageRequestDTO);
        PageResultDTO<BookDTO> pageResulDTO = bookservice.readAll(pageRequestDTO);
        model.addAttribute("result", pageResulDTO);
        // model.addAttribute("reqdto", pageRequestDTO);
    }

    @GetMapping({ "/read", "/modify" })
    public void getRead(Long code, PageRequestDTO pageRequestDTO, Model model) {
        log.info("book get 요청 {}", code);
        BookDTO dto = bookservice.read(code);
        model.addAttribute("dto", dto);

    }

    @PostMapping("/modify")
    public String postModify(BookDTO dto, PageRequestDTO pageRequestDTO, RedirectAttributes rttr) {
        log.info("book modify 요청 {}", dto);
        bookservice.modify(dto);
        rttr.addAttribute("code", dto.getCode());
        rttr.addAttribute("page", pageRequestDTO.getPage());
        rttr.addAttribute("size", pageRequestDTO.getSize());
        rttr.addAttribute("type", pageRequestDTO.getType());
        rttr.addAttribute("keyword", pageRequestDTO.getKeyword());
        return "redirect:/book/read";
    }

    @PostMapping("/remove")
    public String postRemove(Long code, PageRequestDTO pageRequestDTO, RedirectAttributes rttr) {
        log.info("book remove 요청 {}", code);
        // 서비스 호출
        bookservice.remove(code);
        rttr.addAttribute("page", pageRequestDTO.getPage());
        rttr.addAttribute("size", pageRequestDTO.getSize());
        rttr.addAttribute("type", pageRequestDTO.getType());
        rttr.addAttribute("keyword", pageRequestDTO.getKeyword());
        return "redirect:/book/list";
    }

    // 405 : http://localhost:8080/book/remove?code=1

    // @PostMapping("/remove")
    // public void postRemove1(Long code) {
    // log.info("book remove 요청 {}", code);

    // }
    // Caused by: java.lang.IllegalStateException: Ambiguous mapping.
    // Cannot map 'bookController' method
}
