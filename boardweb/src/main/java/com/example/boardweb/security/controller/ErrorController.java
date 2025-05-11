package com.example.boardweb.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ErrorController {

      @GetMapping("/suspended")
      public String accessDenied() {
        return "suspended"; // templates/access-denied.html
     }

       @PostMapping("/suspended") // 에러 처리 뷰 post
      public String accessDeniedPost() {
        return "suspended";
    }
}
