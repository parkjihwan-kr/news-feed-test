package com.pjh.newsfeedtest.board.repository;

import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.board.repository.BoardRepository;
import com.pjh.newsfeedtest.member.domain.Member;
import com.pjh.newsfeedtest.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("[Board] [Repository] findAllByOrderByIdDescTest")
    public void findAllByOrderByIdDescTest(){

    }

    @Test
    @DisplayName("[Board] [Repository] findByIdWithImages")
    public void findByIdWithImagesTest(){
        // given
        Member member = Member.builder()
                .id(1L)
                .username("username1")
                .password("encodedPassword")
                .build();

        Board board = Board.builder()
                .title("test board title")
                .content("test board content")
                .member(member)
                .build();

        memberRepository.save(member);
        Board savedBoard = boardRepository.save(board);

        // when
        Optional<Board> foundBoard = boardRepository.findByIdWithImages(savedBoard.getId());

        // then
        assertTrue(foundBoard.isPresent());
        assertEquals(savedBoard.getId(), foundBoard.get().getId());
        assertEquals("test board title", foundBoard.get().getTitle());
    }

    @Test
    @DisplayName("[Board] [Repository] findAllByOrderByIdDesc")
    public void testFindAllByOrderByIdDesc() {
        // given
        Member member = Member.builder()
                .id(1L)
                .username("username1")
                .password("encdoedPassword")
                .build();

        Board board1 = Board.builder()
                .title("title1")
                .content("content1")
                .member(member)
                .build();

        Board board2 = Board.builder()
                .title("title2")
                .content("content2")
                .member(member)
                .build();

        memberRepository.save(member);
        boardRepository.save(board1);
        boardRepository.save(board2);

        // when
        List<Board> boards = boardRepository.findAllByOrderByIdDesc();

        // then
        assertEquals(2, boards.size());
        assertEquals(board2.getId(), boards.get(0).getId());
        assertEquals(board1.getId(), boards.get(1).getId());
    }
}
