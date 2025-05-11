package com.example.boardweb.security.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.boardweb.security.dto.SuspensionHistoryDTO;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.entity.SuspensionHistory;
import com.example.boardweb.security.repository.MemberRepository;
import com.example.boardweb.security.service.SuspensionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/suspensions")
public class AdminSuspensionController {
    
    private final SuspensionService suspensionService;
    private final MemberRepository memberRepository;


    // @PathVariable 경로에서 특정 리소스 식별자를 명시하고 싶을 때 사용 (RESTful 원칙)
    // 사용자별 정지 이력 조회
    @GetMapping("/{username}") // GET /admin/suspensions/{username} -> 조회용
    public String viewSuspensionHistory(
        @PathVariable("username") String username,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "sort", defaultValue = "startTime") String sort,        
        Model model) {

        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, sort));
        Page<SuspensionHistory> result = suspensionService.searchHistories(member, keyword, pageable);

         List<SuspensionHistoryDTO> dtoList = suspensionService.toDTOList(result.getContent());

        model.addAttribute("member", member);
        model.addAttribute("histories", dtoList);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        return "admin/suspension-history";
    }

    // 정지 이력 단건 삭제
    @PostMapping("/delete/{id}") 
    // POST /admin/suspensions/delete/{id} + username 파라미터
    // 조회 후 삭제한 뒤 파라미터를 유저 페이지로 리디렉션 - 조회, 삭제 후 삭제 값을 페이지로 가져감
    public String deleteHistory(@PathVariable("id") Long id, @RequestParam("username") String username) {
        suspensionService.deleteHistory(id);
        return "redirect:/admin/suspensions/" + username;
    }

    @GetMapping
    public String listAllHistories(Model model) {
    List<SuspensionHistory> all = suspensionService.findAll();
    List<SuspensionHistoryDTO> dtoList = suspensionService.toDTOList(all);
    model.addAttribute("histories", dtoList);
    return "admin/suspension-history";
}
}
