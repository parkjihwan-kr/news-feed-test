package com.pjh.newsfeedtest.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestProfileUpdateDto {

    private String password;
    private String content;
    private String passwordConfirm;
}
