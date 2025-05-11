package com.example.boardweb.security.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DateTimeUtil {
    
    
    public static LocalDateTime parseFlexible(String input) {
        List<DateTimeFormatter> formatters = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (Exception ignored) {}
        }

        throw new IllegalArgumentException("지원하지 않는 날짜 형식입니다: " + input);
    }
}
