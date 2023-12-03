package com.pjh.newsfeedtest.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjh.newsfeedtest.board.controller.BoardRestController;
import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.comment.domain.Comment;
import com.pjh.newsfeedtest.comment.dto.CommentRequestDto;
import com.pjh.newsfeedtest.comment.dto.CommentResponseDto;
import com.pjh.newsfeedtest.comment.service.CommentService;
import com.pjh.newsfeedtest.member.domain.Member;
import com.pjh.newsfeedtest.security.config.WebSecurityConfig;
import com.pjh.newsfeedtest.security.service.MemberDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.SQLOutput;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(
        controllers = {CommentController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser", password = "testPassword")
    @DisplayName("[Comment] [Controller] createCommentTest")
    void createCommentTest() throws Exception {
        // Given
        Long boardId = 1L;
        String content = "Test comment content";

        CommentRequestDto requestDto = CommentRequestDto.builder().content(content).build();

        String username = "testUser";
        String password = "testPassword";

        // 가상 사용자를 생성하는 부분을 업데이트
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.setContext(securityContext);

        MemberDetailsImpl mockMemberDetails = new MemberDetailsImpl(Member.builder()
                .username(username)
                .password(password)
                .build());

        CommentResponseDto mockResponseDto = CommentResponseDto.builder()
                .id(1L)
                .content(content)
                .username(username)
                .build();

        when(commentService.createComment(eq(boardId), any(CommentRequestDto.class), eq(mockMemberDetails)))
                .thenReturn(ResponseEntity.ok(mockResponseDto));

        // When, Then
        mockMvc.perform(post("/api/boards/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[Comment] [Controller] updateComment success")
    @WithMockUser(username = "testUser", roles = "USER")
    void updateCommentSuccessTest() throws Exception {
        // Given
        Long boardId = 1L;
        Long commentId = 1L;
        CommentRequestDto updatedComment = new CommentRequestDto("Updated comment content");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/boards/{boardId}/comments/{commentId}", boardId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedComment))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}
