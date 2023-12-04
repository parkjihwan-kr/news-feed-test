package com.pjh.newsfeedtest.member.repository;

import com.pjh.newsfeedtest.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("[Member] [Repository] findByUsernameTest")
    @Transactional
    public void memberRepositoryFindByUsernameTest() {
        // given
        Member member = Member.builder()
                .username("testUser")
                .password("encodedPassword")
                .content("my Biography").build();

        // when
        memberRepository.save(member);
        Optional<Member> foundMember = memberRepository.findByUsername("testUser");

        // then
        assertTrue(foundMember.isPresent());
        assertEquals("testUser", foundMember.get().getUsername());
    }
}
