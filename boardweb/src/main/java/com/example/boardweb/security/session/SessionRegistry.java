package com.example.boardweb.security.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionRegistry {
    
    // 사용자별 세션을 저장하는 역할 (싱글톤으로 빈 등록)
    private final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    public void addSession(String username, HttpSession session) {
        sessions.put(username, session);
    }

    public void removeSession(String username) {
        sessions.remove(username);
    }

    public HttpSession getSession(String username) {
        return sessions.get(username);
    }
}
