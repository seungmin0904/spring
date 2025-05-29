package com.example.mart.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.example.mart.entity.Item;
import com.example.mart.entity.Member;
import com.example.mart.entity.Order;
import com.example.mart.entity.QItem;
import com.example.mart.entity.QMember;
import com.example.mart.entity.QOrder;
import com.example.mart.entity.QOrderItem;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

public class QueryDslOrderRepositoryImpl extends QuerydslRepositorySupport implements QueryDslOrderRepository {

    public QueryDslOrderRepositoryImpl() {
        super(Order.class);

    }

    @Override
    public List<Member> members() {
        QMember member = QMember.member;
        JPQLQuery<Member> query = from(member);
        query.where(member.name.eq("user1")).orderBy(member.name.desc());
        query.select(member);
        List<Member> list = query.fetch();
        return list;
    }

    // fetch(); 리스트 조회
    // fetchFirst();
    // fetchOne();
    // fetchResults();
    // fetchCount();
    @Override
    public List<Item> items() {
        QItem item = QItem.item;
        JPQLQuery<Item> query = from(item);
        query.where(item.name.contains("티셔츠").and(item.price.gt(200000)));
        query.select(item);

        return query.fetch();

    }

    @Override
    public List<Object[]> joinTest() {
        QOrder order = QOrder.order;
        QMember member = QMember.member;
        QOrderItem orderItem = QOrderItem.orderItem;

        JPQLQuery<Order> query = from(order);
        query.join(member).on(order.member.eq(member));
        query.join(orderItem).on(order.eq(orderItem.order));
        JPQLQuery<Tuple> tuple = query.select(order, member, orderItem);
        List<Tuple> result = tuple.fetch();
        List<Object[]> list = result.stream().map(t -> t.toArray()).collect(Collectors.toList());

        return list;
    }

    @Override
    public List<Object[]> subQueryTest() {
        QOrder order = QOrder.order;
        QMember member = QMember.member;
        QOrderItem orderItem = QOrderItem.orderItem;
        JPQLQuery<Order> query = from(order);
        query.join(member).on(order.member.eq(member))
                .join(orderItem).on(order.eq(orderItem.order));

        JPQLQuery<Long> orderCnt = JPAExpressions.select(orderItem.order.count())
                .from(orderItem)
                .where(orderItem.order.eq(order)).groupBy(orderItem.order);

        JPQLQuery<Integer> orderSum = JPAExpressions.select(orderItem.orderPrice.sum())
                .from(orderItem)
                .where(orderItem.order.eq(order)).groupBy(orderItem.order);
        JPQLQuery<Tuple> tuple = query.select(order, member, orderItem, orderCnt, orderSum);
        List<Tuple> result = tuple.fetch();
        List<Object[]> list = result.stream().map(t -> t.toArray()).collect(Collectors.toList());

        return list;
    }

}
