package com.example.book.dto;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Builder;
import lombok.Data;

@Data
public class PageResultDTO<E> {
    // 화면 보여줄 목록
    private List<E> dtoList;
    // 페이지 번호 목록
    // 전체 개수 / 페이지당 보여줄 개수 정보
    private List<Integer> pageNumList;
    // 요청 들어올때 페이지 번호,사이즈
    private PageRequestDTO pageRequestDTO;
    // 이전 , 다음 버튼 작동 여부
    private boolean prev, next;
    // 나눠서 넣을 전체 페이지 개수 등
    private int totalCount, prevPage, nextPage, totalPage, current;

    @Builder(builderMethodName = "withAll")
    public PageResultDTO(List<E> dtoList, PageRequestDTO pageRequestDTO, long totalCount) {
        // 초기화
        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = (int) totalCount;

        // 화면에 보여주기 위해 계산
        int end = (int) (Math.ceil(pageRequestDTO.getPage() / 10.0)) * 10;
        int start = end - 9;
        int last = (int) (Math.ceil(totalCount / (double) pageRequestDTO.getSize()));
        end = end > last ? last : end;

        this.prev = start > 1;
        this.next = totalCount > end * pageRequestDTO.getSize();
        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        if (prev) {
            this.prevPage = start - 1;
        }
        if (next) {
            this.nextPage = end + 1;
        }
        totalPage = this.pageNumList.size();
        this.current = pageRequestDTO.getPage();
    }
}
