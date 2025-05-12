package com.example.boardweb.security.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.entity.SuspensionHistory;

public interface SuspensionHistoryRepository extends JpaRepository<SuspensionHistory, Long> {

    List<SuspensionHistory> findByMember(Member member);

    List<SuspensionHistory> findByMemberAndStartTimeAfter(Member member, LocalDateTime after);

    Page<SuspensionHistory> findByMember(Member member, Pageable pageable);
}
