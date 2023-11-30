package com.sparta.newsfeed.board.repository;

import com.sparta.newsfeed.board.domain.Board;
import com.sparta.newsfeed.board.dto.BoardRequestDTO;
import com.sparta.newsfeed.member.domain.Member;
import com.sparta.newsfeed.member.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Log4j2
class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;
    @Autowired
    MemberRepository memberRepository;

    @DisplayName("[Board] [Repository] [Insert]")
    @Test
    void testInsert() {
        Optional<Member> result = memberRepository.findById(1L);
        Member member = result.orElseThrow();

        IntStream.rangeClosed(1, 3).forEach(i -> {
            Board board = Board.builder()
                    .title("title..." + i)
                    .content("content..." + i)
                    .member(member)
                    .build();
            Board result1 = boardRepository.save(board);
            log.info("Id: " + result1.getId());
        });
//            List<Board> boards = boardRepository.findAllByOrderByIdDesc();
//        assertThat(boards.size()).isEqualTo(3);
    }

    @Transactional
    @DisplayName("[Board] [Repository] [Update]")
    @Test
    void testUpdate() {
        Long id = 2L;
        String title = "update test title...";
        String content = "update test content";

        Optional<Board> result = boardRepository.findById(id);
        Board board = result.orElseThrow();

        board.change(title, content);
        Board updatedBoard = boardRepository.save(board);

        assertThat(updatedBoard.getTitle()).isEqualTo(title);
    }

    @Transactional
    @DisplayName("[Board] [Repository] [Delete]")
    @Test
    void testDelete() {
        Long id = 1L;
        boardRepository.deleteById(id);
        Optional<Board> result = boardRepository.findById(id);
        assertThatCode(result::orElseThrow).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("[Board] [Repository] [Read]")
    @Test
    void testRead() {
        Long id = 1L;
        Optional<Board> result = boardRepository.findById(id);
        Board board = result.orElseThrow();
        log.info(board);
    }

    @DisplayName("[Board] [Repository] [ReadAll]")
    @Test
    void testReadAll() {
        List<Board> boards = boardRepository.findAllByOrderByIdDesc();
        assertThat(boards.size()).isEqualTo(50);
    }

    @Test
    void name() {
        Optional<Member> result = memberRepository.findById(1L);
        Member member = result.orElseThrow();
        for (int i = 0; i <= 100; i++) {
            Board board = Board.builder()
                    .title("Title..." + i)
                    .member(member)
                    .content("Content..." + i)
                    .build();
            for (int j = 0; j < 3; j++) {
                if (i % 5 == 0) {
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(), i+"file"+j+".jpg");
            }
            boardRepository.save(board);
        }
    }
}