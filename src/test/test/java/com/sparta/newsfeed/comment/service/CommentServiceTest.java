package com.sparta.newsfeed.comment.service;

import com.sparta.newsfeed.comment.dto.CommentRequestDto;
import com.sparta.newsfeed.comment.dto.CommentResponseDto;
import com.sparta.newsfeed.comment.repository.CommentRepository;
import com.sparta.newsfeed.member.domain.Member;
import com.sparta.newsfeed.member.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class CommentServiceTest {
    @Autowired
    CommentService commentService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    void name() {
        Long id = 1L;
        String username = "user...1";
        CommentRequestDto reqDto = CommentRequestDto.builder()
                .content("comment content")
                .build();
        Optional<Member> result = memberRepository.findById(id);
        Member member = result.orElseThrow();
        log.info(member);
//        CommentResponseDto respDto = commentService.createComment(id, reqDto, member);
//        log.info(respDto);
    }

    @Test
    void testCreate() {
        Long userId = 3L;
        Optional<Member> result = memberRepository.findById(userId);
        Member member = result.orElseThrow();
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .content("test")
                .build();
        commentService.createComment(5L, commentRequestDto, member);
    }
}