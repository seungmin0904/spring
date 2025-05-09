package com.example.mart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mart.entity.Item;
import com.example.mart.entity.Member;
import com.example.mart.entity.Order;

public interface QueryDslOrderRepository {

    List<Member> members();

    List<Item> items();

    List<Object[]> joinTest();

    List<Object[]> subQueryTest();

}
