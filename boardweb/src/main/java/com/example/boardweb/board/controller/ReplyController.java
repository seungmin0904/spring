package com.example.boardweb.board.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.boardweb.board.dto.PageRequestDTO;
import com.example.boardweb.board.dto.ReplyRequestDTO;
import com.example.boardweb.board.dto.ReplyWebDTO;
import com.example.boardweb.board.service.ReplyWebService;
import com.example.boardweb.security.dto.MemberSecurityDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;

@Log4j2
@RequestMapping("/boardweb/reply")
@RequiredArgsConstructor
@Controller
public class ReplyController {
    // 댓글 처리 컨트롤러
    private final ReplyWebService replyWebService;

    @GetMapping("/modify")
    public String getModifyForm(@RequestParam Long rno, @RequestParam Long bno, Model model) {
        // 댓글 정보 조회
        ReplyWebDTO dto = replyWebService.readOne(rno);
        model.addAttribute("dto", dto);
        return "boardweb/reply/modify"; // 수정 폼을 띄울 HTML 페이지
    }

    @GetMapping("/register")
    public String getRegisterForm(
            @RequestParam("bno") Long bno,
            @RequestParam(value = "parentRno", required = false) Long parentRno,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            @AuthenticationPrincipal MemberSecurityDTO authUser, // 로그인 사용자 정보
            Model model) {
        log.info(" 로그인 사용자: {}", authUser); // 찍히는지 확인
        ReplyRequestDTO dto = new ReplyRequestDTO();
        dto.setBno(bno);
        dto.setParentRno(parentRno);
        dto.setReplyer(authUser.getName()); // 자동으로 작성자 세팅

        model.addAttribute("dto", dto);
        return "boardweb/reply/register";
    }

    /** 댓글/답글 등록 **/
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("dto") ReplyRequestDTO dto, BindingResult bindingResult,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            RedirectAttributes rttr) {

        log.info("[REPLY CREATE] {}", dto);
        // 서버 측 입력값 검증 조건 미 충족 시 에러 플래시 메세지와 함께
        // read 페이지로 redirect
        if (bindingResult.hasErrors()) {

            rttr.addFlashAttribute("error", "댓글 내용을 입력해주세요.");

            // 페이지 정보도 함께 리다이렉트
            return "redirect:/boardweb/read?bno=" + dto.getBno()
                    + "&page=" + pageRequestDTO.getPage()
                    + "&size=" + pageRequestDTO.getSize()
                    + "&type=" + pageRequestDTO.getType()
                    + "&keyword=" + pageRequestDTO.getKeyword();
        }

        // 조건 통과 후 정상 처리 시 생성 후 read로 redirect
        replyWebService.create(dto);

        return "redirect:/boardweb/read?bno=" + dto.getBno() +
                "&page=" + pageRequestDTO.getPage() +
                "&size=" + pageRequestDTO.getSize() +
                "&type=" + pageRequestDTO.getType() +
                "&keyword=" + pageRequestDTO.getKeyword();
    }

    /** 댓글/답글 수정 **/
    @PostMapping("/modify")
    public String modify(
            ReplyRequestDTO dto,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            RedirectAttributes rttr) {

        log.info("[REPLY MODIFY] {}", dto);
        replyWebService.modify(dto);

        return "redirect:/boardweb/read?bno=" + dto.getBno() +
                "&page=" + pageRequestDTO.getPage() +
                "&size=" + pageRequestDTO.getSize() +
                "&type=" + pageRequestDTO.getType() +
                "&keyword=" + pageRequestDTO.getKeyword();
    }

    /** 댓글/답글 삭제 **/
    @PostMapping("/delete")
    public String delete(
            Long rno,
            Long bno,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            RedirectAttributes rttr) {

        log.info("[REPLY DELETE] rno={}", rno);
        replyWebService.delete(rno);

        return "redirect:/boardweb/read?bno=" + bno +
                "&page=" + pageRequestDTO.getPage() +
                "&size=" + pageRequestDTO.getSize() +
                "&type=" + pageRequestDTO.getType() +
                "&keyword=" + pageRequestDTO.getKeyword();
    }
}