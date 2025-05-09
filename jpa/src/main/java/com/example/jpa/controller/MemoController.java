package com.example.jpa.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.jpa.dto.MemoDTO;
import com.example.jpa.service.MemoService;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;






@RequestMapping("/memo")
@Log4j2
@Controller
@RequiredArgsConstructor
public class MemoController {
    // 서비스 메소드 호출
    // 데이터가 전송된다면 전송된 데이터를 Model 에 담기
    private final MemoService memoService;



    // 주소 설계
    // 전체 memo 조회 : /memo/list
    // @GetMapping("/list")
    // public void getList(Model model) {
    //     List<MemoDTO> list = memoService.getList();
    //     model.addAttribute("list", list);
    // }
    // 
    @GetMapping("/list")
    public String getList(Model model, @PageableDefault(page= 0, size = 10, sort = "mno", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MemoDTO> pageResult = memoService.getListPage(pageable);
        model.addAttribute("list", pageResult.getContent());
        model.addAttribute("page", pageResult);

        return "memo/list";
    }





    // 특정 memo 조회 : /memo/read?mno=3
    // 하는일이 같으면 메소드 이름을 통일 그냥 조회와 update를 하기 전 조회를 묶어서 함수로 만듬
    @GetMapping(value = { "/read" , "/update" }) // (value = { "/read" , "/update" }) => 두개 주소를 처리
    public void getRow(Long mno, Model model) {
        log.info("조회 요청", mno);
        MemoDTO dto = memoService.getRow(mno);
        model.addAttribute("dto", dto);
        // template 에서 ${dto.memoText} 로 사용 가능

    }
    // memo 수정
    @PostMapping("/update")
    public String postUpdate(MemoDTO dto, RedirectAttributes rttr) {
        log.info("메모수정{}",dto);
        Long mno = memoService.memoUpdate(dto);
        // 수정 완료시 read 화면으로 이동
        rttr.addAttribute("mno",mno);
        return "redirect:/memo/read";

    }
    
        

  
    // memo 추가 : /memo/new
    @GetMapping("/new")
    public void getNew() {
        log.info("새 메모 작성 폼 요청 ");
    }

    @PostMapping("/new")
    public String postNew(MemoDTO dto, RedirectAttributes rttr) {
        // 사용자 입력값 가져오기 
        log.info("새 메모 작성", dto);
      Long mno = memoService.memoCreate(dto);
        // 페이지 이동
        rttr.addFlashAttribute("msg", mno);
        return "redirect:/memo/list";
    
    }
    
    // memo 삭제 : /memo/remove?mno=3
    @GetMapping("remove")
    public String getRemove(Long mno) {
        log.info("memo 삭제요청", mno);

        // 삭제 서비스 호출
        memoService.memoDelete(mno);

        return "redirect:/memo/list";
    }
    

}
