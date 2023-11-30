package com.sparta.newsfeed.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberTest {
    @Test
    @DisplayName("[Member] [domain] [Member] [Create]")
    public void createMember(){
        // given
        String username = "username";
        String password = "12341234";
        String content = "content";

        // when
        final Member member = Member.builder()
                .username(username)
                .content(content)
                .password(password)
                .build();

        // then
        assertEquals(username, member.getUsername());
        assertEquals(password, member.getPassword());
        assertEquals(content, member.getContent());
    }

    @Test
    @DisplayName("[Member] [domain] [Member] [changePassword()]")
    public void memberChangePassword(){
        // given
        final Member member = Member.builder()
                .username("username")
                .password("12341234")
                .content("content")
                .build();

        // when
        String changeMyPassword = "123412341";
        member.changePassword(changeMyPassword);

        // then
        assertEquals(changeMyPassword, member.getPassword());
    }

    @Test
    @DisplayName("[Member] [domain] [Member] [changeContent()]")
    public void memberChangeContent(){
        //given
        Member member = Member.builder()
                .username("username")
                .password("12341234")
                .content("content")
                .build();

        //when
        String changeMyContent = "myContent";
        member.changeContent(changeMyContent);

        //then
        assertEquals(changeMyContent, member.getContent());
    }
}
