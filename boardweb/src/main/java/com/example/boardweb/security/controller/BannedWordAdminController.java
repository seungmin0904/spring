package com.example.boardweb.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.boardweb.security.service.BannedWordService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/banned-words")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BannedWordAdminController {
    
    
    private final BannedWordService bannedWordService;

    @GetMapping
    public String viewList(Model model) {
        model.addAttribute("words", bannedWordService.getAll());
        return "admin/banned-words";
    }

    @PostMapping("/add")
    public String add(@RequestParam("word") String word) {
        bannedWordService.add(word);
        return "redirect:/admin/banned-words";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id) {
        bannedWordService.delete(id);
        return "redirect:/admin/banned-words";
    }

}
