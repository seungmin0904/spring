package com.example.boardweb.board.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.boardweb.board.dto.BoardWebDTO;
import com.example.boardweb.board.dto.PageRequestDTO;
import com.example.boardweb.board.dto.PageResultDTO;
import com.example.boardweb.board.service.BoardWebService;
import com.example.boardweb.oauth.dto.OAuthUserDTO;
import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.service.SecurityService;

import jakarta.validation.Valid;

@RequestMapping("/boardweb")
@Log4j2
@Controller
@RequiredArgsConstructor
public class BoardWebController {

    private final BoardWebService boardWebService;
    private final SecurityService securityService;

    // 리스트
    @GetMapping("/list")
    public String list(
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            Model model) {
        log.info(">>> 컨트롤러 검색조건 type={}, keyword={}", pageRequestDTO.getType(), pageRequestDTO.getKeyword());

        PageResultDTO<BoardWebDTO> result = boardWebService.getList(pageRequestDTO);
        model.addAttribute("result", result);
        return "boardweb/list"; // 뷰 이름 (/templates/boardweb/list.html)
    }

    // 등록 폼
    @GetMapping("/register")
    public String registerForm(
            @AuthenticationPrincipal Object principal,
            @ModelAttribute("dto") BoardWebDTO dto,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO) {
        // ─────────────────────────────────────────────
        // DTO에 기본 email 세팅
        // (테스트 용도로 하드코딩, 추후 스프링 시큐리티 사용 시 principal.getName() 으로 대체)
        // dto.setEmail("user1@gmail.com");
        // 로그인 사용자 정보 분기
        if (principal instanceof MemberSecurityDTO user) {
            dto.setEmail(user.getUsername());
            dto.setName(user.getName());
        } else if (principal instanceof OAuthUserDTO social) {
            dto.setEmail(social.getUsername()); // 또는 getEmail()
            dto.setName(social.getName());
        } else {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        return "boardweb/register";
    }

    // ▶ 등록 처리
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("dto") BoardWebDTO dto, BindingResult bindingResult,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            RedirectAttributes rttr) {
        if (bindingResult.hasErrors()) {
            return "boardweb/register";
        }

        log.info("[CREATE] {}", dto);
        Long bno = boardWebService.create(dto);
        rttr.addFlashAttribute("msg", "등록되었습니다. bno=" + bno);
        // 등록 후 리스트로 리다이렉트
        return "redirect:/boardweb/list"
                + "?page=" + pageRequestDTO.getPage()
                + "&size=" + pageRequestDTO.getSize()
                + "&type=" + pageRequestDTO.getType()
                + "&keyword=" + pageRequestDTO.getKeyword();
    }

    // ▶ 상세 조회(Read)
    @GetMapping("/read")
    public String read(
            @RequestParam("bno") Long bno,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            Model model) {
        BoardWebDTO dto = boardWebService.read(bno);
        model.addAttribute("dto", dto);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        return "boardweb/read";
    }

    // 수정 폼 (Modify Form)
    @GetMapping("/modify")
    public String modifyForm(
            @RequestParam("bno") Long bno,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            Model model) {
        BoardWebDTO dto = boardWebService.read(bno);

        // 작성자 검증 (이메일 기준)
        if (!securityService.isOwner(dto.getName())) {
            throw new AccessDeniedException("작성자만 접근할 수 있습니다.");

        }
        model.addAttribute("dto", dto);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        return "boardweb/modify";
    }

    // ▶ 수정 처리 (Modify)
    @PostMapping("/modify")
    public String modify(
            @ModelAttribute("dto") BoardWebDTO dto,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            RedirectAttributes rttr) {
        boardWebService.modify(dto);
        rttr.addFlashAttribute("msg", "수정되었습니다.");
        return "redirect:/boardweb/list"
                + "?page=" + pageRequestDTO.getPage()
                + "&size=" + pageRequestDTO.getSize()
                + "&type=" + pageRequestDTO.getType()
                + "&keyword=" + pageRequestDTO.getKeyword();
    }

    // ▶ 삭제 처리 (Delete)
    @PostMapping("/delete")
    public String delete(
            @RequestParam("bno") Long bno,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            RedirectAttributes rttr) {
        boardWebService.delete(bno);
        rttr.addFlashAttribute("msg", "삭제되었습니다.");
        return "redirect:/boardweb/list"
                + "?page=" + pageRequestDTO.getPage()
                + "&size=" + pageRequestDTO.getSize()
                + "&type=" + pageRequestDTO.getType()
                + "&keyword=" + pageRequestDTO.getKeyword();
    }
}
