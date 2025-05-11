package com.example.boardweb.security.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.boardweb.security.entity.BannedWord;
import com.example.boardweb.security.repository.BannedWordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BannedWordService {
    
    private final BannedWordRepository bannedWordRepository;

    public List<BannedWord> getAll() {
        return bannedWordRepository.findAll();
    }

    public void add(String word) {
        if (!bannedWordRepository.existsByWord(word)) {
            bannedWordRepository.save(BannedWord.builder().word(word).build());
        }
    }

    public void delete(Long id) {
        bannedWordRepository.deleteById(id);
    }
}
