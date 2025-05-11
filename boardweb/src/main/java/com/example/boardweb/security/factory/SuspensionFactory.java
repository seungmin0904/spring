package com.example.boardweb.security.factory;

import java.time.LocalDateTime;

import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.entity.SuspensionHistory;

public class SuspensionFactory {

      public static SuspensionHistory createAuto(Member member, LocalDateTime start, LocalDateTime end, boolean permanent) {
        return SuspensionHistory.builder()
                .member(member)
                .startTime(start)
                .endTime(end)
                .manuallyLifted(false)
                .permanent(permanent)
                .build();
    }

    public static SuspensionHistory createManual(Member member, LocalDateTime start, LocalDateTime end) {
        return SuspensionHistory.builder()
                .member(member)
                .startTime(start)
                .endTime(end)
                .manuallyLifted(true)
                .permanent(false)
                .build();
    }
}
