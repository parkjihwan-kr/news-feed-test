package com.pjh.newsfeedtest.member.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginDto {
    // 제약 조건 추가
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{4,16}$")
    @Column(unique = true)
    private String username;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6,15}$")
    private String password;

}
