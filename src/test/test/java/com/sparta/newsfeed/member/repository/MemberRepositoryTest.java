package com.sparta.newsfeed.member.repository;

import com.sparta.newsfeed.member.domain.Member;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Log4j2
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("[Member] [Repository] [Insert]")
    @Test
    void testInsert() {
        int dataSize = 50;
        IntStream.rangeClosed(1, dataSize).forEach(i -> {
            Member member = Member.builder()
                    .username("user" + i)
                    .password(passwordEncoder.encode("1111"))
                    .content("content..." + i)
                    .build();
            Member result = memberRepository.save(member);
            log.info("user name" + result.getUsername());
        });

        assertThat(memberRepository.countBy()).isEqualTo(dataSize);
    }

    @DisplayName("[Member] [Repository] [Update] [Password]")
    @Transactional
    @Test
    void testPasswordUpdate() {
        Long id = 1L;
        String updatePassword = "1234";

        Optional<Member> result = memberRepository.findById(id);
        Member member = result.orElseThrow();

        member.changePassword(updatePassword);

        Member updatedMember = memberRepository.save(member);

        assertThat(updatedMember.getPassword()).isEqualTo("1234");
    }

    @DisplayName("[Member] [Repository] [Update] [content]")
    @Transactional
    @Test
    void testUpdateContent() {
        Long id = 1L;
        String contentStr = "update test content...";

        Optional<Member> result = memberRepository.findById(id);
        Member member = result.orElseThrow();

        member.changeContent(contentStr);
        Member updatedMember = memberRepository.save(member);

        assertThat(updatedMember.getContent()).isEqualTo(contentStr);
    }
    @DisplayName("[Member] [Repository] [Delete]")
    @Transactional
    @Test
    void testDelete() {
        Long id = 1L;

        memberRepository.deleteById(id);

        Optional<Member> result = memberRepository.findById(id);
        assertThatCode(result::orElseThrow).isInstanceOf(NoSuchElementException.class);
    }

}