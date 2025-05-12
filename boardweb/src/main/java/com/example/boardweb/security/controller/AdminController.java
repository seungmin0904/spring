package com.example.boardweb.security.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.service.AdminMemberService;
import com.example.boardweb.security.service.SecurityService;
import com.example.boardweb.security.service.SuspensionService;
import com.example.boardweb.security.service.WarningService;
import com.example.boardweb.security.session.SessionRegistry;
import com.example.boardweb.security.util.SecurityUtil;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SuspensionService suspensionService;
    private final SecurityService securityService;
    private final AdminMemberService adminMemberService;
    private final SessionRegistry sessionRegistry;
    private final WarningService warningService;

    // 정지 기간 직접입력 컨트롤러
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/suspend-custom")
    public String suspendCustom(
            @RequestParam("username") String username,
            @RequestParam("until") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime until) {
        Member member = adminMemberService.getMembers(username);

        securityService.suspendMember(username, until);
        suspensionService.recordAutoSuspension(member, LocalDateTime.now(), until, false); // 기간 정지로 기록

        // 세션 강제 종료
        HttpSession session = sessionRegistry.getSession(username);
        if (session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException ignored) {
            }
            sessionRegistry.removeSession(username);
        }

        return "redirect:/admin/dashboard";
    }

    // 관리자 대시보드: 회원 목록 + 정지 상태 확인
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("username", SecurityUtil.getCurrentUsername());

        List<Member> members = adminMemberService.getAllMembers();
        model.addAttribute("members", members);

        // 자동 정지 여부 판단 맵 생성
        Map<String, Boolean> autoSuspensionMap = warningService.getAutoSuspensionMap(members);

        model.addAttribute("autoSuspensionMap", autoSuspensionMap);

        return "admin/dashboard";
    }

    // 제재 처리 (정지, 해제, 기간 정지 등)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/suspend")
    public String suspend(
            @RequestParam("username") String username,
            @RequestParam("days") int days // -1: 무기한, 0: 해제, 3/7: 기간 정지
    ) {
        Member member = adminMemberService.getMembers(username);
        if (days == 0) {
            securityService.liftSuspension(username, true);
        } else {
            LocalDateTime until = (days == -1) ? null : LocalDateTime.now().plusDays(days);
            securityService.suspendMember(username, until);
            // 정지 기록(무기한 여부 포함)
            suspensionService.recordAutoSuspension(member, LocalDateTime.now(), until, until == null);
        }

        // 세션이 존재할 경우만 처리 (예외 방지)
        HttpSession session = sessionRegistry.getSession(username);
        if (session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                // 이미 만료된 세션이면 무시
            }
            sessionRegistry.removeSession(username);
        }

        return "redirect:/admin/dashboard";
    }

    // 공통 레이아웃(템플릿) 에서 항상 isAdmin이 필요할 경우 번거로운 model.addAttribute("isAdmin") 삽입 대신
    // @ControllerAdvice 사용으로 전역적으로 등록함

    // @ControllerAdvice
    // public class GlobalModelAdvice {

    // @ModelAttribute("isAdmin")
    // public boolean addIsAdmin() {
    // return SecurityUtil.getCurrentMember() != null &&
    // SecurityUtil.getCurrentMember().getAuthorities().stream()
    // .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    // }
    // }
}
