package com.pjh.newsfeedtest.board.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BoardRepositoryTest {
/*
    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("[Board] [Repository] [Insert]")
    void testInsert() {
        int dataSize = 50;
        Member member = Member.builder().username("user").build();

        IntStream.rangeClosed(1, dataSize).forEach(i -> {
            Board board = Board.builder()
                    .title("title" + i)
                    .content("content" + i)
                    .member(member)
                    .build();
            Board result = boardRepository.save(board);
            log.info("board title: " + result.getTitle());
        });

        List<Board> boards = boardRepository.findAll();
        assertThat(boards).hasSize(dataSize);
    }*/
}
