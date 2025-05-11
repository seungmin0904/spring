package com.example.boardweb.board.repository.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.example.boardweb.board.dto.PageRequestDTO;
import com.example.boardweb.board.entity.BoardWeb;
import com.example.boardweb.board.entity.QBoardWeb;
import com.example.boardweb.board.entity.QReplyWeb;
import com.example.boardweb.security.entity.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SearchBoardRepositoryImpl extends QuerydslRepositorySupport implements SearchBoardRepository {

    public SearchBoardRepositoryImpl() {
        super(BoardWeb.class);
    }

    @Override
    public Page<Object[]> list(PageRequestDTO requestDTO,Pageable pageable) {
        log.info("list() called");

        QBoardWeb boardWeb = QBoardWeb.boardWeb;
        QMember member = QMember.member;
        QReplyWeb replyWeb = QReplyWeb.replyWeb;
        log.info(">>> Repository 검색조건 type={}, keyword={}", requestDTO.getType(), requestDTO.getKeyword());
        // association join: member
        JPQLQuery<BoardWeb> query = from(boardWeb)
        .leftJoin(boardWeb.member, member);

        // ② 검색 조건
    String type = requestDTO.getType();
    String keyword = requestDTO.getKeyword();

    if (type != null && keyword != null && !type.isEmpty() && !keyword.isEmpty()) {
        BooleanBuilder builder = new BooleanBuilder();

        if (type.contains("t")) {
            builder.or(boardWeb.title.containsIgnoreCase(keyword));
        }
        if (type.contains("c")) {
            builder.or(boardWeb.content.containsIgnoreCase(keyword));
        }
        if (type.contains("w")) {
            // 부분 일치하면 검색 허용 
            // builder.or(memberWeb.name.containsIgnoreCase(keyword));
            // 완전 일치해야 검색 허용
            builder.or(member.name.eq(keyword));
        }

        query.where(builder);
    }

        // 서브쿼리: 해당 게시글의 댓글 개수
        SubQueryExpression<Long> countSub = JPAExpressions
        .select(replyWeb.rno.count())
        .from(replyWeb)
        .where(replyWeb.boardWeb.eq(boardWeb));

        // select 절: boardWeb, memberWeb, 그리고 서브쿼리(countSub)만
        JPQLQuery<Tuple> tuple = query
        .select(boardWeb, member, countSub);


        // for 문을 사용하여 정렬 조건 추가
        for (Sort.Order o : pageable.getSort()) {
            Order direction =
                o.isAscending() ? Order.ASC
                                : Order.DESC;
            
            // 정렬 조건에 따라 OrderSpecifier 생성
            // boardWeb.bno, memberWeb.email, countSub에 대한 정렬 조건 추가
            switch (o.getProperty()) {
                case "bno":
                    // bno 정렬
                    tuple.orderBy(new OrderSpecifier<>(direction, boardWeb.bno));
                    break;
                case "email":
                    // member email 정렬
                    tuple.orderBy(new OrderSpecifier<>(direction, member.username));
                    break;
                case "replyCount":
                    // 댓글 개수(countSub) 정렬
                    tuple.orderBy(new OrderSpecifier<>(direction, countSub));
                    break;
                    // 필요하면 다른 케이스 추가
            }
        }
        // 페이징 처리: pageable.getOffset()과 pageable.getPageSize()를 사용하여 offset과 limit 설정
        // if 사용으로 전체 리스트 조회와 페이징 처리 구분
        if (pageable.isPaged()) {
            tuple.offset(pageable.getOffset());
            tuple.limit(pageable.getPageSize());
        }            
        List<Tuple> result      = tuple.fetch();
        long        totalCount  = tuple.fetchCount();
        List<Object[]> content = result.stream()
        .map(Tuple::toArray)
        .collect(Collectors.toList());
        
         return new PageImpl<>(content, pageable, totalCount);
   }
}