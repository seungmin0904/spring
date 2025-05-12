package com.example.boardweb.security.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 자동 정지 해제 이력 저장(해제는 임시 정지 기준)
    @Transactional
    public void recordAutoLift(Member member, LocalDateTime start, LocalDateTime end) {
        SuspensionHistory history = SuspensionFactory.createAuto(member, start, end, false); // permanent = false
        history.setLiftedAt(end); // 실제 해제된 시간
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
        List<SuspensionHistory> histories = suspensionHistoryRepository.findByMemberAndStartTimeAfter(member,
                oneWeekAgo);
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
    public Page<SuspensionHistory> searchAllHistories(String keyword, Pageable pageable) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return suspensionHistoryRepository.findAllWithMember(pageable);
        }
            List<SuspensionHistory> all = suspensionHistoryRepository.findAllWithMember();

            String lower = keyword.toLowerCase();

        // 필터링
        List<SuspensionHistory> filtered = all.stream()
                .filter(history -> 
                            (history.getStartTime() != null && history.getStartTime().toString().toLowerCase().contains(lower)) ||
                            (history.getEndTime() != null && history.getEndTime().toString().toLowerCase().contains(lower)) ||
                            (history.getMember() != null && history.getMember().getUsername().toLowerCase().contains(lower)) ||
                    ("영구".contains(lower) && history.isPermanent()) ||
                    ("수동".contains(lower) && history.isManuallyLifted()))

                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<SuspensionHistory> pageList = (start < end) ? filtered.subList(start, end) : List.of();

        return new PageImpl<>(pageList, pageable, filtered.size());
    }

    @Transactional
    public void liftSuspensionById(Long id, boolean isManual) {
    SuspensionHistory target = suspensionHistoryRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("정지 이력이 존재하지 않습니다."));

    // 이미 해제된 이력이면 중복 방지
    if (target.getLiftedAt() != null) return;

    Member member = target.getMember();
    LocalDateTime start = target.getStartTime();
    LocalDateTime end = target.getEndTime();

    // endTime이 null인 경우 무기한 정지 → now() 기준으로 보정
    if (end == null) {
        end = LocalDateTime.now();
    }

    target.setLiftedAt(LocalDateTime.now());
    suspensionHistoryRepository.save(target); // 기존 이력 업데이트

    SuspensionHistory lifted = isManual
        ? SuspensionFactory.createManual(member, start, end)
        : SuspensionFactory.createAuto(member, start, end, false);

    lifted.setLiftedAt(LocalDateTime.now());
    lifted.setManuallyLifted(isManual);

    suspensionHistoryRepository.save(lifted);

    // 🔁 정지 해제 처리
    member.setSuspended(false);
    member.setSuspendedUntil(null);
}

    public Map<String, Long> getActiveHistoryIdMap(List<Member> members) {
    Map<String, Long> result = new HashMap<>();
    for (Member member : members) {
        suspensionHistoryRepository
            .findTopByMemberAndLiftedAtIsNullOrderByStartTimeDesc(member)
            .ifPresent(history -> result.put(member.getUsername(), history.getId()));
    }
    return result;
   }

    // 정지중 이력 dto 추출
     public List<SuspensionHistoryDTO> getActiveHistories() {
     List<SuspensionHistory> list = suspensionHistoryRepository.findByLiftedAtIsNullOrderByStartTimeDesc();
        return toDTOList(list);

   }

    // 해제된 이력 dto 추출
    public List<SuspensionHistoryDTO> getLiftedHistories() {
        List<SuspensionHistory> list = suspensionHistoryRepository.findByLiftedAtIsNotNullAndManuallyLiftedIsTrueOrderByLiftedAtDesc();

    return toDTOList(list);

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
                        .build())
                .toList();
    }
}
