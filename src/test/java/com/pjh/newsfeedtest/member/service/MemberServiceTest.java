package com.pjh.newsfeedtest.member.service;

import com.pjh.newsfeedtest.member.domain.Member;
import com.pjh.newsfeedtest.member.dto.MemberResponseDTO;
import com.pjh.newsfeedtest.member.dto.RequestProfileUpdateDto;
import com.pjh.newsfeedtest.member.dto.SignupDto;
import com.pjh.newsfeedtest.member.repository.MemberRepository;
import com.pjh.newsfeedtest.security.service.MemberDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("[Member] [Service] [MemberService] signup Seccuess")
    void signupSeccuess() {
        // given
        String username = "testUser";
        String rawPassword = "password123";
        String content = "my Content";

        SignupDto signupDto = new SignupDto(username,rawPassword,content);
        // SignupDto에 들어가는 데이터 입력
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        // when() -> "password123"이라는 입력값을 받으면 passwordEncoder.encode()를 하고
        // thenReturn() -> 해당 입력값에 대해서 encodedPassword로 반환
        when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());
        // when() -> "testUser"라는 입력값을 findByUsername()메서드에서 받으면
        // thenReturn() -> 해당 입력값에 대해서 Optional.empty() 반환, 해당 사용자명은 DB에 존재하지 않음

        // when
        assertDoesNotThrow(() -> memberService.signup(signupDto));
        // 예외처리 검사 memberService.signup(signupDto); 시행

        // then
        verify(passwordEncoder, times(1)).encode(rawPassword);

        verify(memberRepository, times(1)).findByUsername("testUser");
        // memberRepository에서 findByUsername("testUser")를 증명하기 위해
        // signup 메서드 내부에서 사용자명 중복체크가 이루어지고 있는지 확인
        verify(memberRepository, times(1)).save(any(Member.class));
        // membeerRepository에서 save함수 호출 시
        // 메서드가 Member.class의 인자를 받아 한 번 호출되었는지?
    }

    @Test
    @DisplayName("[Member] [Service] [MemberService] signup duplicate Username")
    public void signupDuplicateUsername(){
        // given
        String duplicateUsername = "existingUsername";
        SignupDto signupDto1 = new SignupDto(duplicateUsername, "testPassword", "testContent");
        SignupDto signupDto2 = new SignupDto(duplicateUsername, "anotherPassword", "anotherContent");

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        // existingUser라는 사용자명을 이미 사용중이라고 가정, Optional.of()로 해당 파라미터는 null이 아닐 것을 명시
        when(memberRepository.findByUsername(duplicateUsername)).thenReturn(Optional.of(new Member()));

        // when, then
        assertThrows(IllegalArgumentException.class, () -> memberService.signup(signupDto1));

        // 중복 사용자명으로 가입 시도 시 IllegalArgumentException이 발생하는지 확인
        verify(memberRepository, times(1)).findByUsername(duplicateUsername);
        verify(memberRepository, never()).save(any(Member.class));
        // 중복 사용자명이면 저장되지 않아야 함

        // 추가로 두 번째 사용자명으로 가입 시도도 예외를 던져야 함
        assertThrows(IllegalArgumentException.class, () -> memberService.signup(signupDto2));
    }

    @Test
    @DisplayName("[Member] [Service] [MemberService] updateMember success")
    public void UpdateMemberSuccess(){
        // Given
        Member member = Member.builder()
                .username("testUser")
                .password("encodedPassword")
                .content("my Content").build();
        // 해당 사용자는 회원가입을 한 상태임으로 encodedPassword를 가지고 있는게 맞다
        MemberDetailsImpl memberDetails =  new MemberDetailsImpl(member);
        RequestProfileUpdateDto updateProfileDto = new RequestProfileUpdateDto("newPassword","change Content","newPasswordConfirm");

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("encodedNewPassword");

        // When
        assertDoesNotThrow(() -> memberService.updateMember(updateProfileDto, memberDetails));

        // Then
        verify(passwordEncoder, times(1)).matches("newPassword", "encodedPassword");
        verify(passwordEncoder, times(1)).encode("newPasswordConfirm");
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("[Member] [Service] [MemberService] updateMember password != passwordConfirm")
    void updateMemberPasswordMismatch() {
        // Given
        Member member = Member.builder()
                .username("testUser")
                .password("encodedPassword")
                .content("myContent").build();
        MemberDetailsImpl memberDetails = new MemberDetailsImpl(member);

        RequestProfileUpdateDto updateProfileDto = new RequestProfileUpdateDto();

        // When, Then
        assertThrows(IllegalArgumentException.class,
                () -> memberService.updateMember(updateProfileDto, memberDetails));
    }

    @Test
    @DisplayName("[Member] [Service] [MemberService] readMemberInfo() success")
    public void readMemberInfoSuccess() {
        // given
        String username = "testUser";
        Member testMember = new Member();
        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(testMember));
        when(modelMapper.map(testMember, MemberResponseDTO.class)).thenReturn(new MemberResponseDTO());

        // when
        MemberResponseDTO result = memberService.readMemberInfo(username);

        // then
        assertNotNull(result);
        verify(memberRepository, times(1)).findByUsername(username);
        verify(modelMapper, times(1)).map(testMember, MemberResponseDTO.class);
    }

    @Test
    @DisplayName("[Member] [Service] [MemberService] readMemberInfo() not Found username")
    void readMemberInfoUserNotFound() {
        // Given
        String username = "nonExistingUser";

        when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(NoSuchElementException.class, () -> memberService.readMemberInfo(username));

        // Verification
        verify(memberRepository, times(1)).findByUsername(username);
        verify(modelMapper, never()).map(any(), eq(MemberResponseDTO.class));
    }
}
