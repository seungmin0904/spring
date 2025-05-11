package com.example.boardweb.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.entity.WarningLog;

public interface WarningLogRepository extends JpaRepository<WarningLog,Long> {
    
    List<WarningLog> findByMember(Member member);
    long countByMember(Member member); // 누적 경고 수 계산용
}
