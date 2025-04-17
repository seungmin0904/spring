package com.example.jpa.repository;

import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.jpa.entity.Memo;

@SpringBootTest
public class MemoRepositotyTest {
    @Autowired
    private MemoRepositoty memoRepositoty;

    @Test
    public void insertTest() {
        LongStream.rangeClosed(1, 10).forEach(i -> {
            Memo memo = Memo.builder().memoText("memoText" + i).build();
            memoRepositoty.save(memo);
        });
    }

    @Test
    public void updateTest() {
        Memo memo = memoRepositoty.findById(6L).get();
        memo.setMemoText("memoText update");
        memoRepositoty.save(memo);
    }

    @Test
    public void readTest() {
        Memo memo = memoRepositoty.findById(7L).get();
        System.out.println(memo);
    }

    @Test
    public void listTest() {
        memoRepositoty.findAll().forEach(memo -> System.out.println(memo));
    }

    @Test
    public void deleteTest() {
        memoRepositoty.deleteById(10L);
    }
}
