package com.example.boardweb.security.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.dto.SuspensionHistoryDTO;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.entity.SuspensionHistory;
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

        Map<String, Long> activeHistoryIdMap = suspensionService.getActiveHistoryIdMap(members);
        model.addAttribute("activeHistoryIdMap", activeHistoryIdMap);

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

    @PostMapping("/suspensions/lift/manual/{id}")
    public String manualLift(@PathVariable("id") Long id) {
        // 1. 정지 해제 실행 → username 리턴
        String username = suspensionService.liftSuspensionById(id, true);

        // 2. 현재 로그인한 사용자 정보 확인
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 3. 현재 로그인 사용자가 정지 해제 대상과 같다면 SecurityContext 갱신
        if (principal instanceof MemberSecurityDTO dto &&
                dto.getUsername().equals(username)) {

            // 최신 Member 정보 조회
            Member updated = adminMemberService.getMembers(username);

            // 새 DTO 생성
            MemberSecurityDTO updatedDTO = MemberSecurityDTO.builder()
                    .username(updated.getUsername())
                    .password(updated.getPassword())
                    .name(updated.getName())
                    .emailVerified(updated.isEmailVerified())
                    .suspended(updated.isSuspended())
                    .suspendedUntil(updated.getSuspendedUntil())
                    .roleNames(updated.getRoles().stream()
                            .map(r -> r.getRoleName().name()).collect(Collectors.toSet()))
                    .build();

            // 새로운 인증 정보로 교체
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedDTO,
                    null,
                    updatedDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        return "redirect:/admin/suspensions";
    }

    @GetMapping("/suspensions/active")
    public String viewActiveSuspensions(Model model) {
        List<SuspensionHistoryDTO> list = suspensionService.getActiveHistories();
        model.addAttribute("histories", list);
        model.addAttribute("pageTitle", "현재 정지중인 사용자 이력");
        return "admin/suspension-list";

    }

    @GetMapping("/suspensions/lifted")
    public String viewLiftedSuspensions(Model model) {
        List<SuspensionHistoryDTO> list = suspensionService.getLiftedHistories();
        model.addAttribute("histories", list);
        model.addAttribute("pageTitle", "해제된 사용자 이력");
        return "admin/suspension-list";

    }

    @GetMapping("/withdrawals")
    public String showWithdrawals(Model model) {
        List<Member> withdrawals = securityService.getWithdrawalRequestedMembers();
        model.addAttribute("withdrawals", withdrawals);
        return "admin/withdrawn-member-list"; // 뷰 파일명 그대로 반영
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
