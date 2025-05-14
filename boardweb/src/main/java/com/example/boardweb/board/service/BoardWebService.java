package com.example.boardweb.board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.boardweb.board.dto.BoardRequestDTO;
import com.example.boardweb.board.dto.BoardWebDTO;
import com.example.boardweb.board.dto.PageRequestDTO;
import com.example.boardweb.board.dto.PageResultDTO;
import com.example.boardweb.board.dto.ReplyWebDTO;
import com.example.boardweb.board.entity.BoardWeb;
import com.example.boardweb.board.mapper.BoardWebMapper;
import com.example.boardweb.board.repository.BoardWebRepository;
import com.example.boardweb.board.repository.ReplyWebRepository;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class BoardWebService {
    private final BoardWebRepository boardWebRepository;
    private final MemberRepository memberRepository;
    private final ReplyWebRepository replyWebRepository;

    public PageResultDTO<BoardWebDTO> getList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable(Sort.by("bno").descending());

        Page<Object[]> page = boardWebRepository.list(pageRequestDTO, pageable);
        // Function<T, R> 인터페이스를 사용하여 람다 표현식으로 변환
        Function<Object[], BoardWebDTO> fn = (en -> entityToDto((BoardWeb) en[0],
                (Member) en[1],
                (Long) en[2]));

        List<BoardWebDTO> dtoList = page.getContent().stream()
                .map(fn)
                .collect(Collectors.toList());

        return PageResultDTO.<BoardWebDTO>withAll() // 빌더 시작 (인자 없이)
                .dtoList(dtoList) // dtoList 설정
                .pageRequestDTO(pageRequestDTO) // pageRequestDTO 설정
                .totalCount(page.getTotalElements()) // totalCount 설정
                .build(); // 빌더 최종 호출

    }

    @Transactional(readOnly = true)
    public BoardWebDTO read(Long bno) {
        // 1) 게시글, 회원 정보 조회
        BoardWeb boardWeb = boardWebRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + bno));
        Member member = boardWeb.getMember();

        // 2) 댓글 개수 조회
        Long replyCount = replyWebRepository.countByBoardWeb(boardWeb);

        // 3) 기본 DTO 생성
        BoardWebDTO dto = entityToDto(boardWeb, member, replyCount);

        // ────────── 트리 구조 댓글 로딩 ──────────
        // 4) 댓글 전체를 DTO로 변환
        List<ReplyWebDTO> allReplies = replyWebRepository
                .parentReplyWebs(boardWeb)
                .stream()
                .map(r -> {
                    if (r == null)
                        return null;
                    ReplyWebDTO rd = new ReplyWebDTO();
                    rd.setRno(r.getRno());
                    rd.setParentRno(r.getParent() != null ? r.getParent().getRno() : null);
                    rd.setReplyer(r.getReplyer());
                    rd.setText(r.getText());
                    rd.setCreatedDate(r.getCreatedDate());
                    rd.setDeleted(r.isDeleted());
                    rd.setMoDateTime(r.getUpdatedDate());
                    return rd;
                })
                .collect(Collectors.toList());

        // 5) rno → DTO 맵 생성
        Map<Long, ReplyWebDTO> map = allReplies.stream()
                .collect(Collectors.toMap(ReplyWebDTO::getRno, Function.identity()));

        // 6) 루트 댓글과 자식 연결
        List<ReplyWebDTO> roots = new ArrayList<>();
        for (ReplyWebDTO r : allReplies) {
            if (r == null)
                continue;
            Long parentRno = r.getParentRno();
            if (parentRno == null) {
                roots.add(r);
            } else {
                ReplyWebDTO parentDto = map.get(parentRno);
                if (parentDto != null) {
                    parentDto.getChildren().add(r);
                }
            }
        }

        // 7) DTO에 트리 구조 세팅
        dto.setReplies(roots.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        return dto;
    }

    public Long create(BoardRequestDTO dto) {
        // 로그인 사용자 검증 없이도 요청에 username 포함되어 있어야 함
        String username = dto.getUsername();

        // Member 조회 (username = 이메일)
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + username));

        // Mapper 이용하여 Entity 생성
        BoardWeb boardWeb = BoardWebMapper.toEntity(dto, member);

        // 저장
        BoardWeb saved = boardWebRepository.save(boardWeb);
        return saved.getBno();
    }

    @Transactional
    public void modify(BoardWebDTO dto) {
        BoardWeb board = boardWebRepository.findById(dto.getBno())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + dto.getBno()));

        // Lombok @Setter 사용
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());

        // 추가된부분: 변경된 엔티티를 즉시 저장
        boardWebRepository.save(board);
    }

    @Transactional
    public void delete(Long bno) {
        // 댓글 먼저 삭제 (cascade 옵션 없을 때)
        BoardWeb board = boardWebRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + bno));
        replyWebRepository.deleteByBoardWeb(board); // 추가: 댓글 일괄 삭제

        boardWebRepository.delete(board); // 게시글 삭제
    }

    // 기존 entity → DTO 변환 헬퍼
    private BoardWebDTO entityToDto(BoardWeb boardWeb, Member member, Long replyCount) {
        return BoardWebDTO.builder()
                .bno(boardWeb.getBno())
                .title(boardWeb.getTitle())
                .content(boardWeb.getContent())
                .email(member.getUsername())
                .name(member.getName())
                .replyCount(replyCount)
                .crDateTime(boardWeb.getCreatedDate())
                .moDateTime(boardWeb.getUpdatedDate())
                .build();
    }

}
