package com.example.boardweb.board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.boardweb.board.dto.ReplyRequestDTO;
import com.example.boardweb.board.dto.ReplyWebDTO;
import com.example.boardweb.board.entity.BoardWeb;
import com.example.boardweb.board.entity.ReplyWeb;
import com.example.boardweb.board.repository.BoardWebRepository;
import com.example.boardweb.board.repository.ReplyWebRepository;
import com.example.boardweb.security.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyWebService {
        private final ReplyWebRepository replyRepo;
        private final BoardWebRepository boardRepo;

        // 댓글 전체 조회 (게시글 번호 기준)
        public List<ReplyWebDTO> getReplies(Long bno) {

                BoardWeb board = boardRepo.findById(bno)
                                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + bno));
                List<ReplyWeb> entities = replyRepo.findByBoardWebOrderByCreatedDateAsc(board);

                log.info("▶ 전체 댓글 수 = {}", entities.size());

                // 1. 모든 댓글을 DTO로 변환 + Map에 저장
                Map<Long, ReplyWebDTO> dtoMap = entities.stream()
                                .map(reply -> ReplyWebDTO.builder()
                                                .rno(reply.getRno())
                                                .bno(reply.getBoardWeb().getBno())
                                                .text(reply.getText())
                                                .replyer(reply.getReplyer())
                                                .username(reply.getUsername())
                                                .createdDate(reply.getCreatedDate())
                                                .deleted(reply.isDeleted())
                                                .moDateTime(reply.getUpdatedDate())
                                                .parentRno(reply.getParent() != null ? reply.getParent().getRno()
                                                                : null)
                                                .build())
                                .collect(Collectors.toMap(ReplyWebDTO::getRno, Function.identity()));

                // 2. 계층 구조 조립
                List<ReplyWebDTO> rootReplies = new ArrayList<>();

                for (ReplyWebDTO dto : dtoMap.values()) {
                        if (dto.getParentRno() == null) {
                                rootReplies.add(dto); // 루트 댓글
                        } else {
                                ReplyWebDTO parent = dtoMap.get(dto.getParentRno());
                                if (parent != null) {
                                        parent.getChildren().add(dto); // 자식으로 추가
                                }
                        }
                }

                return rootReplies; // 루트 댓글만 반환 → 자식은 children 통해 재귀

        }

        /** 댓글/답글 등록 **/
        @Transactional
        public void create(ReplyRequestDTO dto) {
                BoardWeb board = boardRepo.findById(dto.getBno())
                                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + dto.getBno()));

                ReplyWeb parent = null;
                if (dto.getParentRno() != null && dto.getParentRno() > 0) {
                        parent = replyRepo.findById(dto.getParentRno())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "부모댓글 없음: " + dto.getParentRno()));
                }

                String currentUsername = SecurityUtil.getCurrentUsername();
                String currentName = SecurityUtil.getCurrentName();

                ReplyWeb reply = ReplyWeb.builder()
                                .boardWeb(board)
                                .replyer(currentName)
                                .username(currentUsername)
                                .text(dto.getText())
                                .parent(parent)
                                .build();

                replyRepo.save(reply);
        }

        /** 댓글/답글 수정 **/
        @Transactional
        public void modify(ReplyRequestDTO dto) {
                ReplyWeb reply = replyRepo.findById(dto.getRno())
                                .orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + dto.getRno()));

                String currentUsername = SecurityUtil.getCurrentUsername();
                if (!reply.getUsername().equals(currentUsername)) {
                        throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
                }
                reply.setText(dto.getText());
                // 변경 감지로 트랜잭션 커밋 시 자동 반영
        }

        /** 댓글/답글 삭제 **/
        @Transactional
        public void delete(Long rno) {
                ReplyWeb reply = replyRepo.findById(rno)
                                .orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + rno));

                String currentUsername = SecurityUtil.getCurrentUsername();
                if (!reply.getUsername().equals(currentUsername)) {
                        throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
                }
                reply.setDeleted(true);
        }

        public ReplyWebDTO readOne(Long rno) {
                ReplyWeb reply = replyRepo.findById(rno)
                                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다: " + rno));

                return ReplyWebDTO.builder()
                                .bno(reply.getBoardWeb().getBno())
                                .rno(reply.getRno())
                                .text(reply.getText())
                                .replyer(reply.getReplyer())
                                .username(reply.getUsername())
                                .createdDate(reply.getCreatedDate())
                                .deleted(reply.isDeleted())
                                .moDateTime(reply.getUpdatedDate())
                                .build();
        }
}