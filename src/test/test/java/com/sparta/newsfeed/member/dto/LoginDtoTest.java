package com.sparta.newsfeed.member.dto;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoginDtoTest {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]{4,16}$";
    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9]{6,15}$";
    @Test
    @DisplayName("[Member] [Dto] [LoginDto] [ValidateMember]")
    public void validateLoginDto(){
        // given
        LoginDto loginDto = new LoginDto("validationTest", "12341234");

        // when
        assertDoesNotThrow(()-> validateLoginDto(loginDto));

        // then
        assertEquals("validationTest", loginDto.getUsername());
        assertEquals("12341234", loginDto.getPassword());
    }

    @Test
    @DisplayName("[Member] [Dto] [LoginDto] [inValidateUsernameLength]")
    public void invalidateSignupDtoUsername1(){
        // given
        LoginDto loginDto = new LoginDto("use", "12341234");

        // when, then
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> validateLoginDto(loginDto));

    }

    @Test
    @DisplayName("[Member] [Dto] [LoginDto] [inValidateUsernameReg]")
    public void invalidateSignupDtoUsername2(){
        // given
        LoginDto loginDto = new LoginDto("!@#$!@#$", "12341234");

        // when, then
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> validateLoginDto(loginDto));

    }

    @Test
    @DisplayName("[Member] [Dto] [SignupDto] [inValidateUsernameNotBlank]")
    public void invalidateSignupDtoUsername3(){
        // given
        LoginDto loginDto = new LoginDto(null, "12341234");

        // when, then
        assertThrows(java.lang.NullPointerException.class, () -> validateLoginDto(loginDto));
    }

    @Test
    @DisplayName("[Memeber] [Dto] [LoginDto] [inValidatePasswordLength]")
    public void invalidatePassword1(){
        // given
        LoginDto loginDto = new LoginDto("user", "1234");

        // when, then
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> validateLoginDto(loginDto));
    }

    @Test
    @DisplayName("[Memeber] [Dto] [LoginDto] [inValidatePasswordReg]")
    public void invalidatePassword2(){
        // given
        LoginDto loginDto = new LoginDto("user", "1234@#$");

        // when, then
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> validateLoginDto(loginDto));
    }

    @Test
    @DisplayName("[Memeber] [Dto] [LoginDto] [inValidatePasswordNotNull]")
    public void invalidatePassword3(){
        // given
        LoginDto loginDto = new LoginDto("user", "1234@#%");

        // when, then
        assertThrows(jakarta.validation.ConstraintViolationException.class, ()-> validateLoginDto(loginDto));
    }
    private void validateLoginDto(LoginDto loginDto){
        // 해당 유저네임의 길이가 작거나 클 때
        if(loginDto.getUsername().length() < 4 || loginDto.getUsername().length() > 16){
            throw new ConstraintViolationException("해당 아이디는 길이가 4미만이거나 16초과입니다.", null);
        }
        // 해당 유저네임의 정규표현식이 알맞지 않을 때
        if(!Pattern.matches(USERNAME_PATTERN, loginDto.getUsername())){
            throw new ConstraintViolationException("해당 유저네임은 정규표현식에 알맞지 않습니다.", null);
        }
        // 해당 유저네임이 null일때
        if(loginDto.getUsername() == null){
            throw new NullPointerException("유저네임은 null입니다.");
        }
        // 해당 패스워드는 길이가 작거나 클 때
        if(loginDto.getPassword().length() < 6 || loginDto.getPassword().length() > 15){
            // @Valid Pattern(regexp = "^[a-zA-z0-9]{6.15}")
            throw new ConstraintViolationException("해당 패스워드는 길이가 6미만거나 15초과입니다.", null);
        }
        // 해당 패스워드가 null이거나 정규표현식에 맞지 않을 때
        if(!Pattern.matches(PASSWORD_PATTERN, loginDto.getPassword())){
            throw new ConstraintViolationException("해당 패스워드는 null이거나 정규 표현식에 알맞지 않습니다.", null);
        }
        // 해당 패스워드가 null일 때
        if(loginDto.getPassword() == null){
            throw new NullPointerException("해당 패스워드는 null입니다.");
        }
    }


    /*
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{4,16}$")
    @Column(unique = true)
    private String username;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6,15}$")
    private String password;*/
}
