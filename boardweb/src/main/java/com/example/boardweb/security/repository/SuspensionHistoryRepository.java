package com.example.boardweb.security.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.entity.SuspensionHistory;

public interface SuspensionHistoryRepository extends JpaRepository<SuspensionHistory, Long> {

    List<SuspensionHistory> findByMember(Member member);

    List<SuspensionHistory> findByMemberAndStartTimeAfter(Member member, LocalDateTime after);

    Page<SuspensionHistory> findByMember(Member member, Pageable pageable);

    @Query("SELECT sh FROM SuspensionHistory sh JOIN FETCH sh.member")
    Page<SuspensionHistory> findAllWithMember(Pageable pageable);

    @Query("SELECT sh FROM SuspensionHistory sh JOIN FETCH sh.member")
    List<SuspensionHistory> findAllWithMember(); // 전체 검색용

     // 정지 이력 ID로 조회 
    Optional<SuspensionHistory> findById(Long id);

    // 현재 정지 중인 이력 1건 (해제 안 된 것만)
    Optional<SuspensionHistory> findTopByMemberAndLiftedAtIsNullOrderByStartTimeDesc(Member member);

    // 정지중 이력
    List<SuspensionHistory> findByLiftedAtIsNullOrderByStartTimeDesc();
    // 해제된 이력
    List<SuspensionHistory> findByLiftedAtIsNotNullAndManuallyLiftedIsTrueOrderByLiftedAtDesc();

    @Query("SELECT sh FROM SuspensionHistory sh JOIN FETCH sh.member WHERE sh.liftedAt IS NULL ORDER BY sh.startTime DESC")
    List<SuspensionHistory> findActiveHistoriesWithMember();

}
