package com.example.boardweb.security.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.boardweb.security.entity.BannedWord;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.entity.WarningLog;
import com.example.boardweb.security.repository.BannedWordRepository;
import com.example.boardweb.security.repository.MemberRepository;
import com.example.boardweb.security.repository.WarningLogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WarningService {
    
    private final WarningLogRepository warningLogRepository;
    private final MemberRepository memberRepository;
    private final BannedWordRepository bannedWordRepository;
    private final SuspensionService suspensionService;

    // 금지어 목록 하드코딩
    // private final Set<String> bannedWords = Set.of("욕설", "비속어", "차별", "금지표현");

    // 게시글/댓글 내용을 검사해서 금지어가 있으면 경고 + 정지
    @Transactional
    public long checkAndWarn(String content, String username) {
    Member member = memberRepository.findById(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

            
    // DB에서 금지어 모두 가져오기
    List<String> bannedWords = bannedWordRepository.findAll()
            .stream()
            .map(BannedWord::getWord)
            .toList();

    boolean warned = bannedWords.stream().anyMatch(content::contains);

    if (!warned) return warningLogRepository.countByMember(member);
        // 1. 경고 1회 부여
        WarningLog log = WarningLog.builder()
                .member(member)
                .reason("금지어 포함된 게시글/댓글 작성")
                .timestamp(LocalDateTime.now())
                .build();
        warningLogRepository.save(log);


        return warningLogRepository.countByMember(member);
      
  }

  // 회원 리스트 받아서 자동 정지 여부 판단 맵 생성 컨트롤러 적용 
  public Map<String, Boolean> getAutoSuspensionMap(List<Member> members) {
        Map<String, Boolean> map = new HashMap<>();

        for (Member member : members) {
            boolean isAutoSuspended = warningLogRepository.countByMember(member) > 0;
            map.put(member.getUsername(), isAutoSuspended);
        }

        return map;
    }

     // 수동 해제 시 경고 초기화 및 이력 기록
    @Transactional
    public void clearWarningsAndRecordManualLift(Member member) {
        warningLogRepository.findByMember(member).forEach(warningLogRepository::delete);
        suspensionService.recordManualLift(member, LocalDateTime.now(), LocalDateTime.now());
    }

    // 외부에서 사용자 객체로 직접 정지 해제 처리할 수 있도록 추가 헬퍼
    @Transactional
    public void handleManualLift(String username) {
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        clearWarningsAndRecordManualLift(member);
    }
    
  }
