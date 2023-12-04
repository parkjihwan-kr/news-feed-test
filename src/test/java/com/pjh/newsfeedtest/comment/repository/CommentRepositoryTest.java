package com.pjh.newsfeedtest.comment.repository;

import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.board.repository.BoardRepository;
import com.pjh.newsfeedtest.comment.domain.Comment;
import com.pjh.newsfeedtest.member.domain.Member;
import com.pjh.newsfeedtest.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Order(1)
    @Transactional
    @DisplayName("[Comment] [Repository] findByBoardOrderByCreatedAtDesc test")
    public void findByBoardOrderByCreatedAtDescTest(){
        // given
        Member boardMember = Member.builder()
                .username("username")
                .password("encodedPassword")
                .build();

        Member commentMember1 = Member.builder()
                .username("commentMemberUsername")
                .password("encodedPassword2")
                .build();

        Member commentMember2 = Member.builder()
                .username("commentMemberUsername")
                .password("encodedPassword2")
                .build();

        Board board = Board.builder()
                .title("title1")
                .content("content1")
                .member(boardMember)
                .build();

        Comment comment1 = Comment.builder()
                .content("comment1")
                .board(board)
                .member(commentMember1)
                .build();

        Comment comment2 = Comment.builder()
                .content("comment2")
                .board(board)
                .member(commentMember2)
                .build();

        memberRepository.save(boardMember);
        boardRepository.save(board);
        memberRepository.save(commentMember1);
        memberRepository.save(commentMember2);
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        Board testBoard = boardRepository.findById(board.getId()).orElseThrow(
                ()-> new NotFoundException("해당 아이디에 존재하는 board가 없습니다.")
        );

        // when
        List<Comment> comments = commentRepository.findByBoardOrderByCreatedAtDesc(testBoard);

        // then
        assertEquals(2, comments.size());
        assertEquals("comment1", comments.get(0).getContent());
        assertEquals("comment2", comments.get(1).getContent());
    }

    @Test
    @Order(2)
    @Transactional
    @DisplayName("[Comment] [Repository] deleteByBoardId")
    public void deleteByBoardIdTest(){
        // given
        Member boardMember = Member.builder()
                .username("username")
                .password("encodedPassword")
                .build();

        Member commentMember1 = Member.builder()
                .username("commentMemberUsername")
                .password("encodedPassword2")
                .build();

        Member commentMember2 = Member.builder()
                .username("commentMemberUsername")
                .password("encodedPassword2")
                .build();

        Board board = Board.builder()
                .title("title1")
                .content("content1")
                .member(boardMember)
                .build();

        Comment comment1 = Comment.builder()
                .content("comment1")
                .board(board)
                .member(commentMember1)
                .build();

        Comment comment2 = Comment.builder()
                .content("comment2")
                .board(board)
                .member(commentMember2)
                .build();

        memberRepository.save(boardMember);
        boardRepository.save(board);
        memberRepository.save(commentMember1);
        memberRepository.save(commentMember2);
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        Board testBoard = boardRepository.findById(board.getId()).orElseThrow(
                ()-> new NotFoundException("해당 아이디에 존재하는 board가 없습니다.")
        );
        // when
        commentRepository.deleteByBoard_Id(board.getId());

        // then
        List<Comment> comments = commentRepository.findByBoardOrderByCreatedAtDesc(board);
        assertEquals(0, comments.size());
    }
}
