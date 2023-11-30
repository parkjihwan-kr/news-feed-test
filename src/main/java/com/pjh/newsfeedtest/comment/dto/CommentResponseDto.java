package com.pjh.newsfeedtest.comment.dto;

import com.pjh.newsfeedtest.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id; // 댓글 번호
    private String content; // 댓글 내용
    private String username; // 작성자
    private LocalDateTime createAt; // 생성일
    private LocalDateTime modifiedAt; // 수정일


    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getMember().getUsername();
        this.createAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
