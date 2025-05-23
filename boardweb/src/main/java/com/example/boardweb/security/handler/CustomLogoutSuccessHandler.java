package com.example.boardweb.security.handler;

import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.session.SessionRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final SessionRegistry sessionRegistry;

    public CustomLogoutSuccessHandler(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO user) {
            sessionRegistry.removeSession(user.getUsername());
        }

        response.sendRedirect("/security/login?logout");
    }
}