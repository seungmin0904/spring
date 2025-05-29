package com.example.jpa.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import com.example.jpa.entity.Item;
import com.example.jpa.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@SpringBootTest
public class ItemRepositoryTest {

        private ItemRepository itemRepository;

        @Autowired
        private EntityManager em;

        @Test
        public void dslTest() {
                JPAQueryFactory queryFactory = new JPAQueryFactory(em);
                QItem item = QItem.item;

                System.out.println(queryFactory.select(item).from(item).where(item.itemNm.eq("price")).fetch());
                System.out.println(queryFactory.select(item).from(item).where(item.itemNm.like("item2%")).fetch());
                System.out.println(queryFactory.select(item).from(item).where(item.itemNm.like("%item2")).fetch());
                System.out.println(queryFactory.select(item).from(item).where(item.itemNm.like("%item2%")).fetch());
                System.out.println(
                                queryFactory.select(item).from(item)
                                                .where(item.itemNm.eq("item").and(item.price.gt(1000))).fetch());
                System.out.println(
                                queryFactory.select(item).from(item)
                                                .where(item.itemNm.eq("item").and(item.price.goe(1000))).fetch());

                // itemNm like '%item2%' or itemSellStatus = SOLDOUT
                System.out.println(queryFactory.select(item).from(item)
                                .where(item.itemNm.like("%item2%").or(item.itemSellStatus.eq(Item.RoleType.SOLDOUT)))
                                .fetch());

                // stockNumber >= 30
                System.out.println(
                                queryFactory.select(item).from(item).where(item.stockNumber.goe(30)));
                // price < 35000
                System.out.println(
                                queryFactory.select(item).from(item).where(item.price.lt(35000)));

                BooleanBuilder builder = new BooleanBuilder();

                builder.and(item.itemNm.eq("item2"));
                builder.and(item.price.gt(1000));

                // // ① 쿼리 실행(fetch)
                List<Item> results = queryFactory
                                .selectFrom(item)
                                .where(builder)
                                .fetch();

                // // ② 검증
                assertFalse(results.isEmpty(), "결과가 비어있으면 안 됩니다");
                assertEquals("item2", results.get(0).getItemNm());
                assertTrue(results.stream().allMatch(i -> i.getPrice() > 1000));

        }
}
