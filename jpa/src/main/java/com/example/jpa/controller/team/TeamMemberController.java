package com.example.jpa.controller.team;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.jpa.dto.team.TeamMemberDTO;
import com.example.jpa.service.teammember.TeamMemberService;
import com.example.jpa.service.teammember.TeamMemberServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RequestMapping("/team")
@Log4j2
@Controller
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

      // 1. 멤버 전체 조회
    @GetMapping("/members")
    public String getAllMembers(Model model) {
        List<TeamMemberDTO> list = teamMemberService.getList();
        model.addAttribute("list", list);
        return "team/memberList"; // 추후 뷰에서 생성
    }

    // 2. 팀 이름으로 멤버 조회
    @GetMapping(value = "/members", params = "teamName")
    public String getMembersByTeamName(@RequestParam String teamName, Model model) {
        List<TeamMemberDTO> list = teamMemberService.getMembersByTeamName(teamName);
        model.addAttribute("list", list);
        model.addAttribute("teamName", teamName);
        return "team/memberList"; // 같은 뷰 재사용
    }

    // 3. 멤버 상세 조회
    @GetMapping("/member/read")
    public String readMember(@RequestParam Long id, Model model) {
        TeamMemberDTO dto = teamMemberService.readMember(id);
        model.addAttribute("dto", dto);
        return "team/memberRead";
    }

    // 4. 멤버 등록 폼 이동
    @GetMapping("/member/register")
    public String showRegisterForm() {
        return "team/memberRegister";
    }

    // 5. 멤버 등록 처리
    @PostMapping("/member/register")
    public String registerMember(TeamMemberDTO dto) {
        teamMemberService.insertMember(dto);
        return "redirect:/team/members";
    }

    // 6. 멤버 수정 폼 이동
    @GetMapping("/member/modify")
    public String showModifyForm(@RequestParam Long id, Model model) {
        TeamMemberDTO dto = teamMemberService.readMember(id);
        model.addAttribute("dto", dto);
        return "team/memberModify";
    }

    // 7. 멤버 수정 처리
    @PostMapping("/member/modify")
    public String modifyMember(TeamMemberDTO dto) {
        teamMemberService.updateMember(dto);
        return "redirect:/team/members";
    }

    // 8. 멤버 삭제
    @PostMapping("/member/delete")
    public String deleteMember(@RequestParam Long id) {
        teamMemberService.deleteMember(id);
        return "redirect:/team/members";
    }
   
    
}
