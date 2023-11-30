package com.pjh.newsfeedtest.comment.controller;

import com.pjh.newsfeedtest.comment.dto.CommentRequestDto;
import com.pjh.newsfeedtest.comment.dto.CommentResponseDto;
import com.pjh.newsfeedtest.comment.service.CommentService;
import com.pjh.newsfeedtest.security.service.MemberDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @Operation(summary = "댓글 작성", description = "로그인한 사용자는 게시글에 댓글을 남길 수 있다.")
    @PostMapping(value = "/boards/{boardId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long boardId, @RequestBody CommentRequestDto commentRequestDto,
                                                            @AuthenticationPrincipal MemberDetailsImpl memberDetails,
                                                            BindingResult bindingResult) throws BindException{
        if(bindingResult.hasErrors()) {
            throw  new BindException(bindingResult);
        }
        return commentService.createComment(boardId, commentRequestDto, memberDetails.getMember());
    }

    /*// 댓글 조회
    @GetMapping("/comment/{boardId}")
    public List<CommentResponseDto> getComments(@PathVariable Long boardId) {
        return commentService.getComments(boardId);
    }*/

    // 댓글 수정
    @Operation(summary = "댓글 수정", description = "로그인한 사용자는 자신의 댓글을 수정할 수 있다.")
    @PutMapping("/boards/{boardId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto commentRequestDto,
                                           @AuthenticationPrincipal MemberDetailsImpl memberDetails,
                                           BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()) {
            throw  new BindException(bindingResult);
        }

        return commentService.updateComment(commentId, commentRequestDto, memberDetails.getMember());
    }
    
    // 댓글 삭제
    @Operation(summary = "댓글 삭제", description = "로그인한 사용자는 자신의 댓글을 삭제할 수 있다.")
    @DeleteMapping("/boards/{boardId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal MemberDetailsImpl memberDetails) {
        return commentService.deleteComment(commentId, memberDetails.getMember());
    }
}
