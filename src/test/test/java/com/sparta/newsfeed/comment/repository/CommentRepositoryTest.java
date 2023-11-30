package com.sparta.newsfeed.comment.repository;

import com.sparta.newsfeed.board.repository.BoardRepository;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    BoardRepository boardRepository;

    @Test
    @Transactional
    @Commit
    public void testRemoveAll() {
        Long id = 1L;
        commentRepository.deleteByBoard_Id(id);
        boardRepository.deleteById(id);
    }
}