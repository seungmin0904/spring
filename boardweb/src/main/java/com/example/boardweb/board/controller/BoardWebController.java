package com.example.boardweb.board.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.example.boardweb.board.dto.BoardRequestDTO;
import com.example.boardweb.board.dto.BoardWebDTO;
import com.example.boardweb.board.dto.PageRequestDTO;
import com.example.boardweb.board.dto.PageResultDTO;
import com.example.boardweb.board.service.BoardWebService;
import com.example.boardweb.board.service.ReplyWebService;
import com.example.boardweb.oauth.dto.OAuthUserDTO;
import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.service.SecurityService;
import com.example.boardweb.security.service.SuspensionService;
import com.example.boardweb.security.service.WarningService;
import com.example.boardweb.security.util.SecurityUtil;

import jakarta.validation.Valid;

@RequestMapping("/boardweb")
@Log4j2
@Controller
@RequiredArgsConstructor
public class BoardWebController {

    private final BoardWebService boardWebService;
    private final SecurityService securityService;
    private final WarningService warningService;
    private final SuspensionService suspensionService;
    private final ReplyWebService replyWebService;

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

    // 등록 처리
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("dto") BoardRequestDTO dto, BindingResult bindingResult,
            @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
            RedirectAttributes rttr) {
        log.info(" 게시글 등록 메서드 진입");

        // 계정 정지 상태 검사
        if (securityService.isSuspended()) {
            throw new AccessDeniedException("계정이 정지되어 글을 작성할 수 없습니다.");
        }

        // 금지어 감지 후 경고 메시지 전달 warn에 담아 뷰에서 출력
        String username = SecurityUtil.getCurrentUsername();
        long count = warningService.checkAndWarn(dto.getContent(), username);

        if (count >= 1) {
            rttr.addFlashAttribute("warn", "⚠️ 금지어가 감지되어 경고 1회가 부여되었습니다. 현재 누적: " + count + "회");
        }

        if (count == 3) {
            LocalDateTime until = LocalDateTime.now().plusDays(7);
            securityService.suspendMember(username, until);
            suspensionService.recordAutoSuspension(
                    securityService.getCurrentMember(), LocalDateTime.now(), until, false);
            rttr.addFlashAttribute("warn", "⚠️ 누적 경고 3회로 7일 정지되었습니다.");
        }

        if (count >= 5 && suspensionService.hasRecentSuspension(securityService.getCurrentMember())) {
            securityService.suspendMember(username, null);
            suspensionService.recordAutoSuspension(
                    securityService.getCurrentMember(), LocalDateTime.now(), LocalDateTime.MAX, true);
            rttr.addFlashAttribute("warn", "🚫 누적 경고 5회 이상으로 영구정지 처리되었습니다.");
        }

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            return "boardweb/register";
        }

        // 게시글 등록처리
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
        dto.setReplies(replyWebService.getReplies(bno));
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
        // if (!securityService.isOwner(dto.getEmail())) {
        // throw new AccessDeniedException("작성자만 접근할 수 있습니다.");

        // }
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

        // 현재 사용자 정지 여부 확인
        if (securityService.isSuspended()) {
            throw new AccessDeniedException("계정이 정지되어 게시글을 수정할 수 없습니다.");

        }

        String username = SecurityUtil.getCurrentUsername();
        long count = warningService.checkAndWarn(dto.getContent(), username);

        if (count >= 1) {
            rttr.addFlashAttribute("warn", "⚠️ 금지어가 감지되어 경고 1회가 부여되었습니다. 현재 누적: " + count + "회");
        }

        if (count == 3) {
            LocalDateTime until = LocalDateTime.now().plusDays(7);
            securityService.suspendMember(username, until);
            suspensionService.recordAutoSuspension(securityService.getCurrentMember(), LocalDateTime.now(), until,
                    false);
            rttr.addFlashAttribute("warn", "⚠️ 누적 경고 3회로 7일 정지되었습니다.");
        }

        if (count >= 5 && suspensionService.hasRecentSuspension(securityService.getCurrentMember())) {
            securityService.suspendMember(username, null);
            suspensionService.recordAutoSuspension(securityService.getCurrentMember(), LocalDateTime.now(),
                    LocalDateTime.MAX, true);
            rttr.addFlashAttribute("warn", "🚫 누적 경고 5회 이상으로 영구정지 처리되었습니다.");
        }

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
