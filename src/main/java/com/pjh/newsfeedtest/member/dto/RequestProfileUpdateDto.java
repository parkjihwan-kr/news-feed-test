package com.pjh.newsfeedtest.member.dto;

import lombok.Getter;

@Getter
public class RequestProfileUpdateDto {

    private String password;
    private String content;
    private String passwordConfirm;
}
