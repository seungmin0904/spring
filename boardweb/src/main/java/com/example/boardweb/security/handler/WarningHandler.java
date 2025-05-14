package com.example.boardweb.security.handler;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.boardweb.security.service.SecurityService;
import com.example.boardweb.security.service.SuspensionService;
import com.example.boardweb.security.service.WarningService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WarningHandler {

    private final SuspensionService suspensionService;
    private final WarningService warningService;
    private final SecurityService securityService;

    public void processContentWarning(String content, String username, RedirectAttributes rttr) {
        long count = warningService.checkAndWarn(content, username);

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
    }
}
