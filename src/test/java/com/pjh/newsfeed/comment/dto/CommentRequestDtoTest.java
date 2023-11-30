package com.pjh.newsfeed.comment.dto;

import com.pjh.newsfeedtest.comment.dto.CommentRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentRequestDtoTest {
    @Test
    @DisplayName("[Comment] [domain] [CommentReuestDto] create")
    public void createCommentRequestDto(){
        // given
        String content = "content";

        // when
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .content(content).build();

        // then
        assertEquals(content, commentRequestDto.getContent());
    }
}
