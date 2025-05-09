package com.example.jpa.repository;

import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.jpa.entity.Memo;

@SpringBootTest
public class MemoRepositotyTest {
    @Autowired
    private MemoRepository memoRepositoty;

    @Test
    public void queryMethodTest() {
        // System.out.println(memoRepositoty.findByMnoLessThan(10L));
        // System.out.println(memoRepositoty.findByMnoLessThanOrderByMnodesc(0L));
        // System.out.println(memoRepositoty.findBymemoTextContaining("asd"));

    }

    @Test
    public void insertTest() {
        LongStream.rangeClosed(1, 10).forEach(i -> {
            Memo memo = Memo.builder().memoText("memoText" + i).build();
            memoRepositoty.save(memo);
        });
    }

    @Test
    public void updateTest() {
        // Memo memo = Memo.builder().mno(1L).memoText("memoText update").build();
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
