package com.example.jpa.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.jpa.dto.MemoDTO;
import com.example.jpa.entity.Memo;
import com.example.jpa.repository.MemoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemoServiceImpl implements MemoService {
    // 메서드 구현 클래스

    // Repository 메소드를 호출 후 결과값 받는 클래스
    // 비즈니스 로직을 처리하는 클래스
    // @Service : 서비스 클래스임을 명시
    // @RequiredArgsConstructor : final 이 붙은 필드에 대한 생성자를 자동으로 생성해준다.

    private final MemoRepository memoRepository;
    private final ModelMapper modelMapper;

    public List<MemoDTO> getList() {
        List<Memo> list = memoRepository.findAll();

        // Memo => MemoDTO 옮기기
        // List<MemoDTO> memos = new ArrayList<>();
        // for (Memo memo : list) {
        // MemoDTO dto = MemoDTO.builder()
        // .mno(memo.getMno())
        // .memoText(memo.getMemoText())
        // .build();
        // memos.add(dto);
        // }

        // list.stream().forEach(memo -> System.out.println(memo));
        List<MemoDTO> memos = list.stream()
                // .map(memo -> entityToDto(memo))
                .map(memo -> modelMapper.map(memo, MemoDTO.class))
                .collect(Collectors.toList());

        return memos;
    }

    // @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    // @Transactional(readOnly = false) // 쓰기 전용 트랜잭션

    // 메모 1개를 조회(read)해서 DTO로 변환 후 리턴
    public MemoDTO getRow(Long mno) {
        Memo memo = memoRepository.findById(mno).orElseThrow(EntityNotFoundException::new);
        // entity => dto
        // modelMapper.map(원본, 변경할타입)
        MemoDTO dto = modelMapper.map(memo, MemoDTO.class);
        return dto;
    }

    // DTO의 데이터를 바탕(read)으로 기존 메모를 수정하고 저장
    public Long memoUpdate(MemoDTO dto) {
        Memo memo = memoRepository.findById(dto.getMno()).orElseThrow(EntityNotFoundException::new);
        memo.changeMemoText(dto.getMemoText());
        // update 실행 => 수정된 Memo return
        memo = memoRepository.save(memo);
        return memo.getMno();
    }

    public void memoDelete(Long mno) {
        memoRepository.deleteById(mno);
    }

    public Long memoCreate(MemoDTO dto) {
        // 새로 입력할 memo 는 MemoDTO 에 저장
        // MemoDTO => Memo 변환
        Memo memo = modelMapper.map(dto, Memo.class);
        // 새로저장한 memo 리턴됨
        memo = memoRepository.save(memo);
        return memo.getMno();
    }

    // DTO => Entity 변환
    private Memo dtoToEntity(MemoDTO memoDTO) {
        Memo memo = Memo.builder()
                .mno(memoDTO.getMno())
                .memoText(memoDTO.getMemoText())
                .build();
        return memo;
    }

    // Entity => DTO 변환
    private MemoDTO entityToDto(Memo memo) {
        MemoDTO dto = MemoDTO.builder()
                .mno(memo.getMno())
                .memoText(memo.getMemoText())
                .createdDate(memo.getCreatedDate())
                .updatedDate(memo.getUpdatedDate())
                .build();

        // MemoDTO dto = new MemoDTO();
        // dto.setMno(memo.getMno());
        // dto.setMemoText(memo.getMemoText());

        return dto;
    }

    // Page<MemoDTO> getListPage(Pageable pageable) : 페이징 처리된 메모 리스트를 가져오는 메소드
    public Page<MemoDTO> getListPage(Pageable pageable) {
        Page<Memo> result = memoRepository.findAll(pageable);
        // Page<MemoDTO> dtoList = result.map(memo -> entityToDto(memo));
        Page<MemoDTO> dtoList = result.map(this::entityToDto);
        return dtoList;
    }

}
