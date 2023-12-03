package com.pjh.newsfeedtest.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjh.newsfeedtest.member.domain.Member;
import com.pjh.newsfeedtest.member.dto.MemberResponseDTO;
import com.pjh.newsfeedtest.member.dto.RequestProfileUpdateDto;
import com.pjh.newsfeedtest.member.service.MemberService;
import com.pjh.newsfeedtest.security.config.WebSecurityConfig;
import com.pjh.newsfeedtest.security.service.MemberDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(
        controllers = {MemberRestController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class MemberRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @InjectMocks
    private MemberRestController memberRestController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("[Member] [Controller] readUserInfoTest")
    void readUserInfoTest() throws Exception {
        // Given
        String username = "testUser";
        String content = "content1";
        MemberResponseDTO mockResponse = MemberResponseDTO.builder()
                .username(username)
                .content(content)
                .build();

        when(memberService.readMemberInfo(username)).thenReturn(mockResponse);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{username}", username))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.memberInfo.username").value(username));
    }

    @Test
    @WithMockUser(username = "testUser", password = "testPassword")
    @DisplayName("[Member] [Controller] modifyContentTest")
    void modifyContentTest() throws Exception {
        // Given
        String username = "testUser";
        String password = "testPassword";
        String content = "update content";
        RequestProfileUpdateDto updateDto = new RequestProfileUpdateDto(password, content, password);

        // 가상 사용자를 생성하는 부분을 업데이트
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.setContext(securityContext);

        MemberDetailsImpl mockMemberDetails = new MemberDetailsImpl(Member.builder()
                .username(username)
                .password(password)
                .build());

        doNothing().when(memberService).updateMember(updateDto, mockMemberDetails);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/{username}", username)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(request -> {
                            request.setRemoteUser(username);
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
       }
}
