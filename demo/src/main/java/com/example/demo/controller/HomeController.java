package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Log4j2
@Controller
public class HomeController {

    // ("/") = http://localhost:8080/
    @GetMapping("/")
    public String getHome() {
        log.info("home 요청");
        return "home";
    }

    // ("/basic") = http://localhost:8080/basic
    @GetMapping("/basic")
    public String getMethodName() {
        return "info";
    }

    // 형변환 자동으로 해줌 원하는 타입 받기 가능
    @PostMapping("/basic")
    public String postAdd(@ModelAttribute("num1") int num1, @ModelAttribute("num2") int num2, Model model) {
        log.info("덧셈 요청 {},{}", num1, num2);
        // 덧셈 결과 info 로 전송 Model(frame) 선언
        int result = num1 + num2;
        // addAttribute("name(중복안되게)" , value)
        model.addAttribute("result", result);
        // model.addAttribute("num1", num1);
        // model.addAttribute("num2", num2);
        return "info";

    }

}
