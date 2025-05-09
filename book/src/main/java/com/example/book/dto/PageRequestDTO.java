package com.example.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
// @Builder.Default : 빌더로 객체 생성 시 필드가 포함 안되는 경우 사용할 기본값 자동지정
// @SuperBuilder : 서브 클래스가 이 클래스를 상속할 때 부모필드도 함께 빌더로 생성
@SuperBuilder
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int size = 10;

    @Builder.Default
    private String sort = "DESC";

    // 검색
    private String type;
    private String keyword;

}
