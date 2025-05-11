package com.example.boardweb.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardweb.security.entity.BannedWord;

public interface BannedWordRepository extends JpaRepository<BannedWord,Long>{
      boolean existsByWord(String word);
}
