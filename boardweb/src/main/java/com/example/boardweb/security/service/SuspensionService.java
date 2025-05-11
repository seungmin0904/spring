package com.example.boardweb.security.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.boardweb.security.dto.SuspensionHistoryDTO;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.entity.SuspensionHistory;
import com.example.boardweb.security.factory.SuspensionFactory;
import com.example.boardweb.security.repository.SuspensionHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuspensionService {
    
    private final SuspensionHistoryRepository suspensionHistoryRepository;

    @Transactional
    public List<SuspensionHistory> findAll() {
    return suspensionHistoryRepository.findAll(Sort.by(Sort.Direction.DESC, "startTime"));
    }


     // 자동 정지 시작 이력 저장
     
    @Transactional
    public void recordAutoSuspension(Member member, LocalDateTime start, LocalDateTime end, boolean permanent) {
        SuspensionHistory history = SuspensionFactory.createAuto(member, start, end, permanent);
        suspensionHistoryRepository.save(history);
    }

    // 자동 정지 해제 이력 저장
    @Transactional
    public void recordAutoLift(Member member, LocalDateTime start, LocalDateTime end) {
    SuspensionHistory history = SuspensionFactory.createAuto(member, start, end, false); // permanent = false
    history.setLiftedAt(end); // 해제된 시간
    suspensionHistoryRepository.save(history);
    }
    
    // 수동 정지 해제 기록 저장
    @Transactional
    public void recordManualLift(Member member, LocalDateTime start, LocalDateTime end) {
        SuspensionHistory history = SuspensionFactory.createManual(member, start, end);
        history.setLiftedAt(end); // 해제된 시간
        suspensionHistoryRepository.save(history);
    }



    // 최근 해제 후 7일 이내 정지 기록 확인
    public boolean hasRecentSuspension(Member member) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<SuspensionHistory> histories = suspensionHistoryRepository.findByMemberAndStartTimeAfter(member, oneWeekAgo);
        return !histories.isEmpty();
    }

    // 사용자 전체 정지 이력 조회 (관리자 용)
    public List<SuspensionHistory> getHistories(Member member) {
        return suspensionHistoryRepository.findByMember(member);
    }

    // 관리자용 정지 이력 삭제
    @Transactional
    public void deleteHistory(Long id) {
        suspensionHistoryRepository.deleteById(id);
    }

    // 정지이력 검색 및 정렬 페이징
     public Page<SuspensionHistory> searchHistories(Member member, String keyword, Pageable pageable) {

        Page<SuspensionHistory> originalPage = suspensionHistoryRepository.findByMember(member, pageable);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return originalPage;
        }

        // 필터링
        List<SuspensionHistory> filtered = originalPage.stream()
            .filter(history ->
                    String.valueOf(history.getStartTime()).contains(keyword) ||
                    String.valueOf(history.getEndTime()).contains(keyword) ||
                    (history.isPermanent() && "영구".contains(keyword)) ||
                    (history.isManuallyLifted() && "수동".contains(keyword))
            )
            .toList();

            return new PageImpl<>(filtered, pageable, filtered.size());
    }

    public List<SuspensionHistoryDTO> toDTOList(List<SuspensionHistory> histories) {
    LocalDateTime now = LocalDateTime.now();

    return histories.stream()
        .map(h -> SuspensionHistoryDTO.builder()
                .id(h.getId())
                .startTime(h.getStartTime())
                .endTime(h.getEndTime())
                .liftedAt(h.getLiftedAt())
                .manuallyLifted(h.isManuallyLifted())
                .permanent(h.isPermanent())
                .active(h.getLiftedAt() == null && (h.getEndTime() == null || h.getEndTime().isAfter(now)))
                .username(h.getMember().getUsername())
                .build()
        ).toList();
  }
}
