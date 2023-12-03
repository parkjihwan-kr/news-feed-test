package com.pjh.newsfeedtest;

import com.pjh.newsfeedtest.board.repository.BoardRepository;
import com.pjh.newsfeedtest.comment.repository.CommentRepository;
import com.pjh.newsfeedtest.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = NewsFeedApplicationTests.class)
@ActiveProfiles("test")
class NewsFeedApplicationTests {

    @Test
    void contextLoads() {
    }
}
