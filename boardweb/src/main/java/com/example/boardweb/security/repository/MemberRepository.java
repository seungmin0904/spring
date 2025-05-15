package com.example.boardweb.security.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.boardweb.security.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username); // ID 중복 체크

    @Query("SELECT DISTINCT m FROM Member m LEFT JOIN FETCH m.roles WHERE m.username = :username")
    Optional<Member> findWithRolesByUsername(@Param("username") String username);

    // 정지 해제 자동감지 쿼리
    List<Member> findAllBySuspendedTrueAndSuspendedUntilBefore(LocalDateTime now);

    // 철회 검증 식
    boolean existsByUsernameAndWithdrawalRequestedAtIsNotNull(String username);

    // 스케쥴러 메서드
    List<Member> findByWithdrawalRequestedAtBefore(LocalDateTime limit);

    // 탈퇴 신청자 로그
    List<Member> findByWithdrawalRequestedAtIsNotNullOrderByWithdrawalRequestedAtAsc();
}
