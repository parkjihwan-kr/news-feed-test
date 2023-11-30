package com.pjh.newsfeed.comment.dto;

import com.pjh.newsfeedtest.comment.dto.CommentResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentResponseDtoTest {

    @Test
    @DisplayName("[Comment] [domain] [CommentResponseDto] create")
    public void createCommentResponseDto(){

        // given
        String content = "content";
        String username = "username";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime modifiedAt = LocalDateTime.now();

        // when
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .content(content)
                .username(username)
                .createAt(createdAt)
                .modifiedAt(modifiedAt)
                .build();

        // then
        assertEquals(content, commentResponseDto.getContent());
        assertEquals(username, commentResponseDto.getUsername());
        assertEquals(createdAt, commentResponseDto.getCreateAt());
        assertEquals(modifiedAt, commentResponseDto.getModifiedAt());
    }
}
