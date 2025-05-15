package com.example.boardweb.security.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.boardweb.board.service.BoardWebService;
import com.example.boardweb.oauth.dto.OAuthUserDTO;
import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.service.EmailService;
import com.example.boardweb.security.service.SecurityService;
import com.example.boardweb.security.util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Controller
@RequestMapping("/security")
@Log4j2
public class MemberSecurityController {
    private final SecurityService securityService;
    private final EmailService emailService; // 이메일 서비스 추가
    private final BoardWebService boardWebService; // 정보수정 테이블 분리 저장용

    @GetMapping("/register")
    public String registerForm(@RequestParam(required = false) String username, Model model) {
        boolean verified = false;

        if (username != null) {
            verified = securityService.isEmailVerified(username); // 회원가입 시 이메일 인증 여부 확인
        }

        model.addAttribute("memberDTO", MemberSecurityDTO.builder().username(username).build());
        model.addAttribute("emailVerified", verified);
        return "security/register";

    }

    // 이메일 인증 발송 처리
    @PostMapping("/request-verification")
    public String requestVerification(@RequestParam("username") String username, RedirectAttributes rttr) {
        securityService.sendInitialVerification(username); // 메일 발송
        rttr.addFlashAttribute("info", "인증 이메일을 발송했습니다.");
        return "redirect:/security/register?username=" + username;
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute MemberSecurityDTO dto, BindingResult bindingResult, Model model,
            RedirectAttributes rttr, HttpServletRequest request, HttpServletResponse response) {
        log.info("회원가입 정보: {}", dto);
        if (bindingResult.hasErrors()) {
            log.warn(" BindingResult 에러 발생: {}", bindingResult);
            model.addAttribute("memberDTO", dto);
            return "security/register"; // 폼 검증 실패 처리
        }

        try {
            securityService.register(dto, request, response); // ㅉ파라미터 수행하는 호출부
            rttr.addFlashAttribute("success", "회원가입이 완료되었습니다.");
            return "redirect:/boardweb/list"; // 자동 로그인 이후 경로 설정
        } catch (IllegalStateException e) {
            log.error(" 회원가입 중 에러 발생: {}", e.getMessage(), e); // 전체 예외 스택까지 출력
            model.addAttribute("memberDTO", dto);
            model.addAttribute("error", e.getMessage()); // 중복 오류 메시지 처리
            return "security/register";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "security/login"; // resources/templates/security/login.html 렌더링

    }

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal Object principal, Model model) {

        if (principal instanceof MemberSecurityDTO member) {
            model.addAttribute("memberDTO", member);
            model.addAttribute("loginType", "local");
        } else if (principal instanceof OAuthUserDTO social) {
            model.addAttribute("memberDTO", social);
            model.addAttribute("loginType", "social");
        } else {
            log.warn("예상치 못한 Principal: {}", principal.getClass().getName());
        }

        return "security/mypage";
    }

    @PostMapping("/mypage")
    public String postMyPage(@ModelAttribute MemberSecurityDTO dto,
            RedirectAttributes rttr) {
        try {
            securityService.updateMember(dto);
            rttr.addFlashAttribute("success", "회원 정보가 수정되었습니다.");

        } catch (Exception e) {
            log.error("회원 정보 수정 중 오류 발생", e); // 내부 로그는 디테일하게!
            rttr.addFlashAttribute("error", "예상치 못한 에러가 발생했습니다.");
        }
        return "redirect:/security/mypage";
    }

    // email 인증 테스트용 메소드
    // @GetMapping("/email-test")
    // @ResponseBody
    // public String emailTest() {
    // emailService.sendTestEmail("test@user.com", "이메일 테스트", "테스트 메일 본문입니다.");
    // return "메일 발송 완료!";

    // }

    // 이메일 인증 확인 처리
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token, RedirectAttributes rttr) {
        boolean result = securityService.verifyEmailToken(token);

        if (result) {
            // info 메시지를 인증 완료로 세팅
            rttr.addFlashAttribute("info", "이메일 인증이 완료되었습니다.");
            return "redirect:/security/register?username=" + securityService.getEmailFromToken(token);
        } else {
            rttr.addFlashAttribute("error", "유효하지 않거나 만료된 인증 링크입니다.");
            return "redirect:/security/register";
        }
    }

    // 새 탭에서 인증 메시지만 출력
    @GetMapping("/verify-info")
    public String verifyEmailInfo(@RequestParam("token") String token, Model model) {
        boolean result = securityService.verifyEmailToken(token);
        log.info("/verify-info 호출됨. 토큰: {}", token); // 이 로그가 찍히는지 확인
        log.info("검증 결과: {}", result);
        model.addAttribute("verified", result);
        return "security/verify"; // 새 탭에서 보여줄 페이지
    }

    // 2. 인증 상태 AJAX 확인용 엔드포인트 추가
    @GetMapping("/check-verified")
    @ResponseBody
    public Map<String, Object> checkVerified(@RequestParam String username) {
        boolean verified = securityService.isEmailVerified(username);
        return Map.of("verified", verified);
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@AuthenticationPrincipal MemberSecurityDTO dto, RedirectAttributes rttr) {
        securityService.resendVerification(dto.getUsername());
        rttr.addFlashAttribute("success", "새 인증 메일이 발송되었습니다.");
        return "redirect:/security/mypage";

    }

    // 비밀번호 찾기 처리 컨트롤러

    // 이메일 입력, 인증 전송 처리
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "security/forgot-password"; // 이메일 입력 폼
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, RedirectAttributes rttr) {
        try {
            securityService.sendResetPasswordLink(email);
            rttr.addFlashAttribute("message", "비밀번호 재설정 링크를 이메일로 보냈습니다.");
        } catch (Exception e) {
            rttr.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/security/forgot-password";
    }

    // 인증 완료 후 비밀번호 재설정 처리

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "security/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes rttr) {
        try {
            securityService.resetPassword(token, newPassword);
            rttr.addFlashAttribute("message", "비밀번호가 변경되었습니다. 로그인하세요.");
            return "redirect:/security/login";
        } catch (Exception e) {
            rttr.addFlashAttribute("error", e.getMessage());
            return "redirect:/security/forgot-password";
        }
    }

    @GetMapping("/need-verification")
    public String needVerificationPage() {
        return "security/need-verification";

    }

    @PostMapping("/member/withdraw")
    public String withdraw(HttpServletRequest request, RedirectAttributes rttr) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            rttr.addFlashAttribute("msg", "로그인이 필요한 작업입니다.");
            return "redirect:/login";
        }
        securityService.requestWithdrawal(username);

        // 로그아웃 처리
        try {
            request.logout();
        } catch (ServletException e) {
            e.printStackTrace(); // 필요 시 로깅
        }

        rttr.addFlashAttribute("msg", "탈퇴 신청이 완료되었습니다. 30일 후 계정이 삭제됩니다.");
        return "redirect:/login"; // 로그인 페이지로 이동
    }

    // 🔧 MemberSecurityController.java 내부
    @GetMapping("/withdraw-cancel")
    public String showWithdrawCancelPage(Model model) {
        model.addAttribute("msg", "회원님은 탈퇴 신청 상태입니다. 탈퇴를 철회하려면 아래 버튼을 눌러주세요.");
        return "security/withdraw-cancel"; // 템플릿 존재해야 함
    }

    @PostMapping("/withdraw-cancel")
    public String cancelWithdrawal(HttpServletRequest request, RedirectAttributes rttr) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            rttr.addFlashAttribute("msg", "로그인이 필요한 작업입니다.");
            return "redirect:/login";
        }

        // 철회 전에 탈퇴 신청 상태인지 확인
        if (!securityService.isWithdrawRequested(username)) {
            rttr.addFlashAttribute("msg", "현재 탈퇴 신청 상태가 아닙니다.");
            return "redirect:/boardweb/list";
        }

        securityService.cancelWithdrawal(username);
        rttr.addFlashAttribute("msg", "탈퇴 신청이 철회되었습니다.");
        return "redirect:/boardweb/list";
    }

    @GetMapping("/withdraw-info")
    public String withdrawalInfoPage(Model model) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null || !securityService.isWithdrawRequested(username)) {
            return "redirect:/boardweb/list"; // 비정상 접근 방지
        }

        model.addAttribute("msg", "회원님은 탈퇴를 신청하신 상태입니다. 철회하려면 아래 버튼을 눌러주세요.");
        return "security/withdraw-info";
    }
}
