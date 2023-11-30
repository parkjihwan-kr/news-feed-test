package com.sparta.newsfeed.member.dto;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SignupDtoTest {
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]{4,16}$";
    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9]{6,15}$";
    @Test
    @DisplayName("[Member] [Dto] [SingupDto] [ValidateMember]")
    public void validateSignupDto(){
        // given
        SignupDto signupDto = new SignupDto("validationTest", "12341234", "content");

        // when
        assertDoesNotThrow(()-> validateSignupDto(signupDto));

        // then
        assertEquals("validationTest", signupDto.getUsername());
        assertEquals("12341234", signupDto.getPassword());
        assertEquals("content", signupDto.getContent());
    }

    @Test
    @DisplayName("[Member] [Dto] [SignupDto] [inValidateUsernameLength]")
    public void invalidateSignupDtoUsername1(){
        // given
        SignupDto signupDto = new SignupDto("","12341234","content");

        // when, then
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> validateSignupDto(signupDto));

    }

    @Test
    @DisplayName("[Member] [Dto] [SignupDto] [inValidateUsernameReg]")
    public void invalidateSignupDtoUsername2(){
        // given
        SignupDto signupDto = new SignupDto("use","12341234","content");

        // when, then
        assertThrows(jakarta.validation.ConstraintViolationException.class, ()-> validateSignupDto(signupDto));
    }

    @Test
    @DisplayName("[Member] [Dto] [SignupDto] [inValidateUsernameNotBlank]")
    public void invalidateSignupDtoUsername3(){
        // given
        SignupDto signupDto = new SignupDto(null,"12341234","content");

        // when, then
        assertThrows(java.lang.NullPointerException.class, () -> validateSignupDto(signupDto));
    }

    @Test
    @DisplayName("[Memeber] [Dto] [SignupDto] [inValidatePasswordLength]")
    public void invalidatePassword1(){
        // given
        SignupDto signupDto = new SignupDto("validateUser", "1234","content");

        // when, then
        assertThrows(jakarta.validation.ConstraintViolationException.class, ()-> validateSignupDto(signupDto));
    }

    @Test
    @DisplayName("[Memeber] [Dto] [SignupDto] [inValidatePasswordReg]")
    public void invalidatePassword2(){
        // given
        SignupDto signupDto = new SignupDto("validateUser", "123@4","content");

        // when, then
        assertThrows(jakarta.validation.ConstraintViolationException.class, ()-> validateSignupDto(signupDto));
    }

    @Test
    @DisplayName("[Memeber] [Dto] [SignupDto] [inValidatePasswordNotNull]")
    public void invalidatePassword3(){
        // given
        SignupDto signupDto = new SignupDto("validateUser", null,"content");

        // when, then
        assertThrows(jakarta.validation.ConstraintViolationException.class, ()-> validateSignupDto(signupDto));
    }
    private void validateSignupDto(SignupDto signupDto){
        // 해당 유저네임의 길이가 작거나 클 때
        if(signupDto.getUsername().length() < 4 || signupDto.getUsername().length() > 16){
            throw new ConstraintViolationException("해당 아이디는 길이가 4미만이거나 16초과입니다.", null);
        }
        // 해당 유저네임의 정규표현식이 알맞지 않을 때
        if(!Pattern.matches(USERNAME_PATTERN, signupDto.getUsername())){
            throw new ConstraintViolationException("해당 유저네임은 정규표현식에 알맞지 않습니다.", null);
        }
        // 해당 유저네임이 null일때
        if(signupDto.getUsername() == null){
            throw new NullPointerException("유저네임은 null입니다.");
        }
        // 해당 패스워드는 길이가 작거나 클 때
        if(signupDto.getPassword().length() < 6 || signupDto.getPassword().length() > 15){
            // @Valid Pattern(regexp = "^[a-zA-z0-9]{6.15}")
            throw new ConstraintViolationException("해당 패스워드는 길이가 6미만거나 15초과입니다.", null);
        }
        // 해당 패스워드가 null이거나 정규표현식에 맞지 않을 때
        if(!Pattern.matches(PASSWORD_PATTERN, signupDto.getPassword())){
            throw new ConstraintViolationException("해당 패스워드는 null이거나 정규 표현식에 알맞지 않습니다.", null);
        }
        // 해당 패스워드가 null일 때
        if(signupDto.getPassword() == null){
            throw new NullPointerException("해당 패스워드는 null입니다.");
        }
    }
    /*
    // 제약 조건 추가
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{4,16}$")
    @Column(unique = true)
    private String username;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6,15}$")
    private String password;

    private String content;*/
}
