package com.sparta.newsfeed.board.domain;

import com.sparta.newsfeed.board.repository.BoardRepository;
import com.sparta.newsfeed.file.domain.BoardImage;
import com.sparta.newsfeed.member.domain.Member;
import com.sparta.newsfeed.member.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class BoardTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;

    @Test
    @Transactional
    void testInsertWithImages() {
        Optional<Member> result = memberRepository.findByUsername("user1");
        Member member = result.orElseThrow();

        Board board = Board.builder()
                .title("Image test")
                .content("첨부파일 테스트")
                .member(member)
                .build();
        for (int i = 0; i < 3; i++) {
            board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");
        }
        boardRepository.save(board);
    }

    @Test
    @Transactional
    void testReadWithImages() {
        Optional<Board> result = boardRepository.findByIdWithImages(1L);
        Board board = result.orElseThrow();

        log.info(board);
        log.info("---------");
        for(BoardImage boardImage : board.getImageSet()) {
            log.info(boardImage);
        }
    }

    @Transactional
    @Commit
    @Test
    void name() {

        Optional<Board> result = boardRepository.findById(1L);
        Board board = result.orElseThrow();

        board.clearImages();
        for(int i = 0; i < 2; i++) {
            board.addImage(UUID.randomUUID().toString(), "updateFile" + i + "jpg");
        }
        boardRepository.save(board);
    }
}


