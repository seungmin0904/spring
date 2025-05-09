package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.CalcDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Log4j2

@Controller
public class AddController {
    @GetMapping("/calc")
    public void getCalc() {
        log.info("calc 페이지 요청");

    }

    @PostMapping("/calc")
    public String postCalc(CalcDTO calcDTO) {
        log.info("calc 연산 요청 {}", calcDTO);
        int result = 0;
        switch (calcDTO.getOp()) {
            case "+":
                result = calcDTO.getNum1() + calcDTO.getNum2();
                break;

            case "-":
                result = calcDTO.getNum1() - calcDTO.getNum2();

                break;
            case "*":
                result = calcDTO.getNum1() * calcDTO.getNum2();

                break;
            case "/":
                result = calcDTO.getNum1() / calcDTO.getNum2();

                break;
            default:
                break;
        }
        log.info("연산결과 {} {} {} = {} ", calcDTO.getNum1(), calcDTO.getOp(), calcDTO.getNum2(), result);
        calcDTO.setResult(result);
        // result 페이지 에서 결과값 보여주기 return "페이지명"
        // localhost:8080/calc + void => calc.html
        // localhost:8080/calc + String => return "result"

        return "result";
    }

}
