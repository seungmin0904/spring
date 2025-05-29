package com.example.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jpa.entity.Memo;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    // where mno < 5
    // List<Memo> findByMnoLessThan(Long mno);

    // // // where mno < 10 order by mno desc
    // List<Memo> findByMnoLessThanOrderByMnodesc(Long mno);

    // // // where memoText like '%memo%'
    // List<Memo> findBymemoTextContaining(String memoText);
}
