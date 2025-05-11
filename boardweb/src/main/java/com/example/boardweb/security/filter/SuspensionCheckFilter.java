package com.example.boardweb.security.filter;

import com.example.boardweb.security.dto.MemberSecurityDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO user) {
            if (user.isSuspended()) {
                LocalDateTime until = user.getSuspendedUntil();
                if (until == null || until.isAfter(LocalDateTime.now())) {
                    // 강제 로그아웃
                    new SecurityContextLogoutHandler().logout(request, response, authentication);
                    response.sendRedirect("/security/login?error=" +
                    java.net.URLEncoder.encode("해당 계정은 정지 상태입니다. 관리자에게 문의하세요.", StandardCharsets.UTF_8));
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}