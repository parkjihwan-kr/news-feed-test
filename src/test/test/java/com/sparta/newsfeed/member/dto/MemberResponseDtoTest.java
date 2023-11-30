package com.sparta.newsfeed.member.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberResponseDtoTest {
    @Test
    @DisplayName("[Member] [Dto] [MemberResponseDto] [Create]")
    public void createMemberResponseDto(){
        // given
        String username = "user";
        String content = "content";

        // when
        MemberResponseDTO memberResponseDTO = new MemberResponseDTO(username, content);

        // then
        assertEquals(username, memberResponseDTO.getUsername());
        assertEquals(content, memberResponseDTO.getContent());
    }
}
