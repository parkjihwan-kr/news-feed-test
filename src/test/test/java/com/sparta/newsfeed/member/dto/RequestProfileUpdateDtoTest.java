package com.sparta.newsfeed.member.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestProfileUpdateDtoTest {
    @Test
    @DisplayName("[Member] [Dto] [RequestProfileUpdateDto] [Create]")
    public void createRequestProfileUpdateDto(){
        // given
        String content = "content";
        String password = "12341234";
        String passwordConfirm = "12341234";

        // when
        RequestProfileUpdateDto requestProfileUpdateDto = new RequestProfileUpdateDto();

        // then
        assertEquals(content, requestProfileUpdateDto.getContent());
        assertEquals(password, requestProfileUpdateDto.getPassword());
        assertEquals(passwordConfirm, requestProfileUpdateDto.getPasswordConfirm());
    }
}
