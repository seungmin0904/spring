package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.MemberDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Log4j2
@Controller
@RequestMapping("/member")
public class MemberController {
    @GetMapping("/register")
    public void getRegister() {

        log.info("회원가입");
    }

    @PostMapping("/register")
    public String postRegister(@ModelAttribute("mDTO") MemberDTO memberDTO, RedirectAttributes rttr) {
        log.info("회원 가입 요청 {},{},{}",
                memberDTO.getUserid(), memberDTO.getPassword(), memberDTO.isCheck());
        // 회원가입이 완료가 되면 로그인 페이지로 이동

        // redirect로 움직이면서 값을 보내고 싶다면?
        // RedirectAttributes 변수명;
        rttr.addAttribute("userid", memberDTO.getUserid());
        rttr.addFlashAttribute("password", memberDTO.getPassword());

        return "redirect:/member/login";
    }

    @GetMapping("/login")
    public void getLogin() {
        log.info("로그인 페이지 요청");
    }

    // postLogin() 괄호 안에 입력값을 불러올 html의 요소의 이름과 그 요소와 맞는 타입을 넣어줌
    @PostMapping("/login")
    // public void postLogin(String userid, String password)
    public void postLogin(LoginDTO loginDTO) {
        log.info("로그인 요청 {},{}", loginDTO.getUserid(), loginDTO.getPassword());

    }

    @GetMapping("/logout")
    public void getLogout() {
        log.info("로그아웃");
    }

    @GetMapping("/change")
    public void getChange() {
        log.info("정보수정");
    }
}
