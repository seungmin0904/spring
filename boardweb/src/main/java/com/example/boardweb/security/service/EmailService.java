package com.example.boardweb.security.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendTestEmail(String to, String subject, String text) {
        log.info("메일 발송 시도: to={}, subject={}", to, subject);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("gogo90490@gmail.com"); // https://moakt.com/ko/inbox 실제 이메일을 활용해 인증을 도와주는 사이트
        mailSender.send(message);
    }
}
