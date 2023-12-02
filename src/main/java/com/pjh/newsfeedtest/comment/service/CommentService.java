package com.pjh.newsfeedtest.comment.service;

import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.board.repository.BoardRepository;
import com.pjh.newsfeedtest.comment.domain.Comment;
import com.pjh.newsfeedtest.comment.dto.CommentRequestDto;
import com.pjh.newsfeedtest.comment.dto.CommentResponseDto;
import com.pjh.newsfeedtest.comment.repository.CommentRepository;
import com.pjh.newsfeedtest.member.domain.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    // 댓글 작성
    @Transactional
    public ResponseEntity<CommentResponseDto> createComment(Long boardId, CommentRequestDto commentRequestDto, Member member) {
        // 게시글 있는 지 확인
        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("게시글이 존재하지 않습니다."));
        log.info(board);

        Comment comment = new Comment(member, commentRequestDto, board);

        log.info("save");
        commentRepository.save(comment);

        return ResponseEntity.status(HttpStatus.OK).body(new CommentResponseDto(comment));
    }

    // 댓글 수정
    @Transactional
    public ResponseEntity<?> updateComment(Long commentId, CommentRequestDto commentRequestDto, Member member) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 댓글 작성자와 입력값 작성자 비교, 일치하면 ㄱㄱ
        if (comment.getMember().getUsername().equals(member.getUsername())) {
            comment.update(commentRequestDto);
            return ResponseEntity.status(HttpStatus.OK).body(new CommentResponseDto(comment));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("작성자만 수정할 수 있습니다.");
        }
    }

    // 댓글 삭제
    public ResponseEntity<?> deleteComment(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 댓글 작성자와 입력값 작성자 비교, 일치하면 ㄱㄱ
        if (comment.getMember().getUsername().equals(member.getUsername())) {
            commentRepository.delete(comment);
            return ResponseEntity.status(HttpStatus.OK).body("댓글을 삭제하였습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("작성자만 삭제할 수 있습니다.");
        }
    }
}
