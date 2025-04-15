package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class SampleListWrapper {
    
    // Controller에서 @ModelAttribute로 전달받은 객체를 담기 위한 Wrapper 클래스
    // Controller 내부에서 List<SampleDTO> list; 형태로 선언하면, @ModelAttribute로 전달받은 객체를 담을 수 없다
    // 따라서, @ModelAttribute로 전달 받은 객체를 담기 위해서는 Wrapper 클래스를 만들어 List<SampleDTO> list = listWrapper.getList(); 형태로 선언해야 한다.
    private List<SampleDTO> list;
}
