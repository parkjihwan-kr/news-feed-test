package com.pjh.newsfeedtest.comment.domain;

import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.comment.dto.CommentRequestDto;
import com.pjh.newsfeedtest.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {
    @Test
    @DisplayName("[Comment] [domain] [Comment] create")
    public void createComment(){
        // given
        String content = "content";
        Board board = new Board();
        Member member = new Member();

        // when
        Comment comment = Comment.builder()
                .content(content)
                .board(board)
                .member(member)
                .build();

        // then
        assertEquals(content, comment.getContent());
        assertEquals(member, comment.getMember());
        assertEquals(board, comment.getBoard());
    }

    @Test
    @DisplayName("[Comment] [domain] [Comment] [update()]")
    public void testUpdate(){
        // given
        Comment comment = Comment.builder()
                .content("content")
                .board(new Board())
                .member(new Member())
                .build();

        CommentRequestDto commentRequestDto = new CommentRequestDto("myContent");

        // when
        comment.update(commentRequestDto);

        // then
        assertEquals(commentRequestDto.getContent(), comment.getContent());
    }
}
