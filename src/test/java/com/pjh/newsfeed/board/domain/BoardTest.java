package com.pjh.newsfeed.board.domain;

import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.file.domain.BoardImage;
import com.pjh.newsfeedtest.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    @Test
    @DisplayName("[Board] [domain] [Board] Create")
    public void createBoard(){
        // given
        String title = "title";
        String content = "content";
        final Member member = Member.builder()
                .username("username")
                .password("12341234")
                .content("content")
                .build();
        Set<BoardImage> images = new HashSet<>(Arrays.asList(
                BoardImage.builder().uuid("uuid1").fileName("image1.jpg").ord(1).build(),
                BoardImage.builder().uuid("uuid2").fileName("image2.jpg").ord(0).build()
        ));
        // when
        Board board = Board.builder()
                .title(title)
                .content(content)
                .member(member)
                .imageSet(images)
                .build();

        // then
        assertEquals(title, board.getTitle());
        assertEquals(content, board.getContent());
        assertEquals(member, board.getMember());
        assertNotNull(board);
        assertNotNull(board.getMember());
        assertEquals(images, board.getImageSet());
    }

    @Test
    @DisplayName("[Board] [domain] [Board] [addImage()]")
    public void testAddImage(){
        Set<BoardImage> images = new HashSet<>(Arrays.asList(
                BoardImage.builder().uuid("uuid1").fileName("image1.jpg").ord(1).build(),
                BoardImage.builder().uuid("uuid2").fileName("image2.jpg").ord(0).build()
        ));

        final Member member = Member.builder()
                .username("username")
                .password("password")
                .content("content")
                .build();

        Board board = Board.builder()
                .title("title")
                .content("content")
                .member(member)
                .imageSet(images)
                .build();

        // when
        board.addImage("uuid1","image1.jpg");

        // then
        BoardImage boardImage = board.getImageSet().iterator().next();
        assertEquals("uuid1", boardImage.getUuid());
        assertEquals("image1.jpg", boardImage.getFileName());
        assertEquals(board, boardImage.getBoard());
    }

    @Test
    @DisplayName("[Board] [domain] [Board] [clearImages()]")
    public void testClearImage(){
        Set<BoardImage> images = new HashSet<>(Arrays.asList(
                BoardImage.builder().uuid("uuid1").fileName("image1.jpg").ord(1).build(),
                BoardImage.builder().uuid("uuid2").fileName("image2.jpg").ord(0).build()
        ));

        final Member member = Member.builder()
                .username("username")
                .password("password")
                .content("content")
                .build();

        Board board = Board.builder()
                .title("title")
                .content("content")
                .member(member)
                .imageSet(images)
                .build();

        // when
        board.clearImages();

        // then
        assertTrue(board.getImageSet().isEmpty());

        board.getImageSet().forEach(boardImage ->
                assertNull(boardImage.getBoard())
        );
    }
}
