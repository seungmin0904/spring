package com.example.boardweb.security.filter;

import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.repository.MemberRepository;
import com.example.boardweb.security.util.SecurityUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class SuspensionCheckFilter extends OncePerRequestFilter {

     private final MemberRepository memberRepository;

    public SuspensionCheckFilter(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO user) {

            // 🔍 항상 DB에서 최신 상태 조회
            Member updated = memberRepository.findWithRolesByUsername(user.getUsername()).orElse(null);

            if (updated != null) {
                System.out.println("[DEBUG] DB 기준 suspended: " + updated.isSuspended() +
                        ", until: " + updated.getSuspendedUntil());

                // ✅ 자동 정지 해제 처리
                if (updated.isSuspended() &&
                        updated.getSuspendedUntil() != null &&
                        updated.getSuspendedUntil().isBefore(LocalDateTime.now())) {
                    updated.setSuspended(false);
                    updated.setSuspendedUntil(null);
                    memberRepository.save(updated);

                    System.out.println("[DEBUG] 자동 해제 완료됨");
                }

                System.out.println("[DEBUG] 최종 검사 대상 suspended = " + updated.isSuspended());
System.out.println("[DEBUG] 최종 검사 대상 until = " + updated.getSuspendedUntil());
System.out.println("[DEBUG] 현재 시간 = " + LocalDateTime.now());
                // ❌ 여전히 정지 상태라면 차단
                if (updated.isSuspended() &&
                        (updated.getSuspendedUntil() == null || updated.getSuspendedUntil().isAfter(LocalDateTime.now()))) {

                    System.out.println("[DEBUG] 정지된 사용자 접근 차단 → " + updated.getUsername());

                    new SecurityContextLogoutHandler().logout(request, response, authentication);
                    response.sendRedirect("/security/login?error=" +
                       java.net.URLEncoder.encode("suspension", StandardCharsets.UTF_8));
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);

    }
}