package com.example.boardweb.board.controller;

import org.springframework.security.access.AccessDeniedException;
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
import com.example.boardweb.security.handler.WarningHandler;
import com.example.boardweb.security.service.SecurityService;
import com.example.boardweb.security.util.SecurityUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;

@Log4j2
@RequestMapping("/boardweb/reply")
@RequiredArgsConstructor
@Controller
public class ReplyController {

    private final SecurityService securityService;
    private final ReplyWebService replyWebService;
    private final WarningHandler warningHandler;
    // 댓글 처리 컨트롤러

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
        dto.setUsername(authUser.getUsername());

        model.addAttribute("dto", dto);
        return "boardweb/reply/register";
    }

    /** 댓글/답글 등록 **/
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("dto") ReplyRequestDTO dto, BindingResult bindingResult,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            RedirectAttributes rttr) {

        // 계정 정지 상태 검사
        if (securityService.isSuspended()) {
            throw new AccessDeniedException("계정이 정지되어 댓글을 작성할 수 없습니다.");
        }

        String username = SecurityUtil.getCurrentUsername();
        String replyer = SecurityUtil.getCurrentName();
        dto.setUsername(username);
        dto.setReplyer(replyer);
        log.info("[REPLY CREATE] {}", dto);
        log.info("🟡 댓글 등록 시 username = '{}'", username);
        warningHandler.processContentWarning(dto.getText(), username, rttr);
        // 서버 측 입력값 검증 조건 미 충족 시 에러 플래시 메세지와 함께
        // read 페이지로 redirect
        if (bindingResult.hasErrors()) {
            rttr.addFlashAttribute("error", "댓글 내용을 입력해주세요.");

            // 페이지 정보도 함께 리다이렉트
            return redirectToRead(dto.getBno(), pageRequestDTO);
        }

        // 조건 통과 후 정상 처리 시 생성 후 read로 redirect
        replyWebService.create(dto);

        return redirectToRead(dto.getBno(), pageRequestDTO);
    }

    /** 댓글/답글 수정 **/
    @PostMapping("/modify")
    public String modify(
            ReplyRequestDTO dto,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            RedirectAttributes rttr) {

        log.info("[REPLY MODIFY] {}", dto);

        if (securityService.isSuspended()) {
            throw new AccessDeniedException("계정이 정지되어 댓글을 수정할 수 없습니다.");

        }
        ReplyWebDTO original = replyWebService.readOne(dto.getRno());
        if (!original.getUsername().equals(SecurityUtil.getCurrentUsername())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        warningHandler.processContentWarning(dto.getText(), original.getUsername(), rttr);
        replyWebService.modify(dto);

        return redirectToRead(dto.getBno(), pageRequestDTO);
    }

    /** 댓글/답글 삭제 **/
    @PostMapping("/delete")
    public String delete(
            Long rno,
            Long bno,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            RedirectAttributes rttr) {

        log.info("[REPLY DELETE] rno={}", rno);

        ReplyWebDTO original = replyWebService.readOne(rno);
        if (!original.getUsername().equals(SecurityUtil.getCurrentUsername())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        replyWebService.delete(rno);

        return redirectToRead(bno, pageRequestDTO);
    }

    // redirct 헬퍼
    private String redirectToRead(Long bno, PageRequestDTO pageRequestDTO) {
        return "redirect:/boardweb/read?bno=" + bno +
                "&page=" + pageRequestDTO.getPage() +
                "&size=" + pageRequestDTO.getSize() +
                "&type=" + pageRequestDTO.getType() +
                "&keyword=" + pageRequestDTO.getKeyword();
    }
}
