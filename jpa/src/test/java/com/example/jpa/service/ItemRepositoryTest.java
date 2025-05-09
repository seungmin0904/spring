package com.example.jpa.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.jpa.entity.Item;
import com.example.jpa.entity.Item.RoleType;
import com.example.jpa.repository.ItemRepository;

@SpringBootTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void insertTest() {
        IntStream.rangeClosed(1, 50).forEach(i -> {
            Item item = Item.builder()
                    .itemNm("item" + i)
                    .price(i + 2200)
                    .stockNumber(i + 10)
                    .itemDetail("item detail" + i)
                    .itemSellStatus(RoleType.SELL)
                    .build();
            itemRepository.save(item);
        });
    }

    @Test
    public void avgTest() {
        List<Object[]> result = itemRepository.asd();

        for (Object[] objects : result) {
            System.out.println(Arrays.toString(objects));
            System.out.println("아이템 수  :" + objects[0]);
            System.out.println("아이템 가격합  :" + objects[1]);
            System.out.println("아이템 가격 평균  :" + objects[2]);
            System.out.println("아이템 가격 최대값  :" + objects[3]);
            System.out.println("아이템 최소값  :" + objects[4]);
        }
    }
}
