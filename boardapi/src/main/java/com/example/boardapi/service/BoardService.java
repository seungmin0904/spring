package com.example.boardapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.boardapi.dto.BoardRequestDTO;
import com.example.boardapi.dto.BoardResponseDTO;
import com.example.boardapi.dto.BoardWithRepliesDTO;
import com.example.boardapi.dto.PageRequestDTO;
import com.example.boardapi.dto.PageResultDTO;
import com.example.boardapi.dto.ReplyDTO;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;
import com.example.boardapi.entity.Reply;
import com.example.boardapi.mapper.BoardMapper;
import com.example.boardapi.mapper.ReplyMapper;
import com.example.boardapi.repository.BoardRepository;
import com.example.boardapi.repository.MemberRepository;
import com.example.boardapi.repository.ReplyRepository;
import com.example.boardapi.security.service.SecurityService;
import com.example.boardapi.util.HtmlUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final SecurityService securityService;
    private final MemberRepository memberRepository;
    private final ReplyRepository replyRepository;
    private final BoardMapper boardMapper;
    private final ReplyMapper replyMapper;

    // 게시글 등록
    public Board register(BoardRequestDTO dto, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("회원 정보 없음"));

        Board board = BoardMapper.toEntity(dto, member);
        boardRepository.save(board);
        return board;
    }

    // 전체 게시글 조회
    public PageResultDTO<BoardResponseDTO> getAll(PageRequestDTO pageRequestDTO) {
        Sort.Direction direction = pageRequestDTO.getSort().equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = pageRequestDTO.getPageable(Sort.by(direction, "createdDate"));
        Page<Board> result = boardRepository.search(pageRequestDTO, pageable);

        return new PageResultDTO<>(result.map(BoardMapper::toDTO));
    }

    // 게시글 단건 조회
    public BoardResponseDTO get(Long bno) {
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));
        return BoardMapper.toDTO(board);
    }

    // 게시글 수정
    public void modify(Long bno, BoardRequestDTO dto, String username) {
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("수정할 게시글이 없습니다"));

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        securityService.checkBoardOwnership(board, member);
        // 기존 content에서 <img> 제거
        String rawContent = dto.getContent();
        String cleanedContent = rawContent.replaceFirst("(?i)<img[^>]*>", "");

        // 새로운 썸네일 추출 및 timestamp 추가
        String thumbnail = HtmlUtils.extractFirstImageUrl(rawContent);
        String thumbnailWithTimestamp = thumbnail != null
                ? thumbnail + "?t=" + System.currentTimeMillis()
                : null;

        // 대표 이미지 + 본문 조합
        String newContent = (thumbnail != null ? "<img src='" + thumbnailWithTimestamp + "'>" : "") + cleanedContent;
        log.info("🖼️ 최종 저장될 썸네일 URL = {}", thumbnailWithTimestamp);

        board.setTitle(dto.getTitle());
        board.setContent(newContent);

        boardRepository.save(board);
    }

    // 게시글 삭제
    public void delete(Long bno, String username) {
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 게시글이 없습니다"));
        if (board.getMember() == null) {
            throw new IllegalStateException("게시글에 작성자 정보가 없습니다.");
        }

        System.out.println("✅ 로그인 사용자: " + username);
        System.out.println("✅ 게시글 작성자: " + board.getMember().getUsername());

        if (!board.getMember().getUsername().equals(username)) {
            throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
        }
        boardRepository.deleteById(bno);
    }

    // 게시글 + 댓글 트리 전체 조회
    public BoardWithRepliesDTO getBoardWithReplies(Long bno) {
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        List<Reply> replies = replyRepository.findByBoardOrderByCreatedDateAsc(board);
        List<ReplyDTO> replyDTOs = replies.stream()
                .map(replyMapper::toDTO)
                .collect(Collectors.toList());

        List<ReplyDTO> treeReplies = buildReplyTree(replyDTOs);

        return BoardWithRepliesDTO.builder()
                .board(BoardMapper.toDTO(board)) // static 방식
                .replies(treeReplies)
                .build();
    }

    // 댓글 트리 구성
    private List<ReplyDTO> buildReplyTree(List<ReplyDTO> flatList) {
        Map<Long, ReplyDTO> dtoMap = flatList.stream()
                .collect(Collectors.toMap(ReplyDTO::getRno, dto -> dto));

        List<ReplyDTO> roots = new ArrayList<>();

        for (ReplyDTO dto : flatList) {
            if (dto.getParentRno() != null) {
                ReplyDTO parent = dtoMap.get(dto.getParentRno());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            } else {
                roots.add(dto);
            }
        }

        return roots;
    }
}
