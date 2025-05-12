package com.example.boardweb.security.factory;

import java.time.LocalDateTime;

import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.entity.SuspensionHistory;

public class SuspensionFactory {

  public static SuspensionHistory createAuto(Member member, LocalDateTime start, LocalDateTime end, boolean permanent) {
    return SuspensionHistory.builder()
        .member(member)
        .startTime(start != null ? start : LocalDateTime.now())
        .endTime(permanent ? null : end)
        .manuallyLifted(false)
        .permanent(permanent)
        .build();
  }

  public static SuspensionHistory createManual(Member member, LocalDateTime start, LocalDateTime end) {
    if (end == null) {
      throw new IllegalArgumentException("수동 정지 시 endTime은 필수입니다.");
    }

    return SuspensionHistory.builder()
        .member(member)
        .startTime(start != null ? start : LocalDateTime.now())
        .endTime(end)
        .manuallyLifted(true)
        .permanent(false)
        .build();
  }
}
