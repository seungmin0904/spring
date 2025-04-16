package com.example.jpa.controller;

import org.springframework.stereotype.Controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
public class HomeController {
    @GetMapping("/home")
    public String getHome() {
        log.info("home 요청");
        return "home";
    }

    @GetMapping("/main")
    public String getMain() {
        log.info("main 요청");
        return "main";
    }

}
