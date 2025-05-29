package com.example.mart.repository;

import java.util.List;

import com.example.mart.entity.Item;
import com.example.mart.entity.Member;

public interface QueryDslOrderRepository {

    List<Member> members();

    List<Item> items();

    List<Object[]> joinTest();

    List<Object[]> subQueryTest();

}
