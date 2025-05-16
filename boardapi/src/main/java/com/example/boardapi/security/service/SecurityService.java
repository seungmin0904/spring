package com.example.boardapi.security.service;

import org.springframework.stereotype.Service;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {
    // 작성자 권한 검증
    public void checkBoardOwnership(Board board, Member currentUser) {
        if (!board.getMember().getUsername().equals(currentUser.getUsername())) {
            throw new SecurityException("작성자만 수정/삭제할 수 있습니다.");
        }
    }
}
