package com.pjh.newsfeedtest.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjh.newsfeedtest.board.controller.BoardRestController;
import com.pjh.newsfeedtest.board.dto.BoardListAllDTO;
import com.pjh.newsfeedtest.board.dto.BoardRequestDTO;
import com.pjh.newsfeedtest.board.dto.BoardResponseDTO;
import com.pjh.newsfeedtest.file.domain.BoardImage;
import com.pjh.newsfeedtest.file.dto.BoardImageDTO;
import com.pjh.newsfeedtest.member.controller.MemberRestController;
import com.pjh.newsfeedtest.member.domain.Member;
import com.pjh.newsfeedtest.security.config.WebSecurityConfig;
import com.pjh.newsfeedtest.security.service.MemberDetailsImpl;
import com.pjh.newsfeedtest.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@WebMvcTest(
        controllers = {BoardRestController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class BoardRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private BoardRestController boardRestController;

    @Test
    @WithMockUser(username = "testUser", password = "testPassword")
    @DisplayName("[Board] [Controller] registerBoardTest")
    void registerTest() throws Exception {
        // Given
        // BoardRequestDto, bindingResult, memberDetails
        String title = "testTitle";
        String content = "testContent";
        List<String> fileNames = new ArrayList<>();
        fileNames.add("file1.png");
        fileNames.add("file2.png");

        BoardRequestDTO requestDTO = BoardRequestDTO.builder()
                .title(title)
                .content(content)
                .fileNames(fileNames)
                .build();

        String username = "testUser";
        String password = "testPassword";

        // 가상 사용자를 생성하는 부분을 업데이트
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.setContext(securityContext);

        // MemberDetails가 null이라는데?
        MemberDetailsImpl mockMemberDetails = new MemberDetailsImpl(Member.builder()
                .username(username)
                .password(password)
                .build());

        doNothing().when(boardService).register(requestDTO, mockMemberDetails);

        // 유효성 검사 실패를 시뮬레이션하기 위해 BindingResult를 설정
        BindingResult bindingResult = new BeanPropertyBindingResult(requestDTO, "boardRequestDTO");
        bindingResult.rejectValue("content", "NotEmpty"); // 예시로 content 필드를 실패로 설정

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/boards/")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // 유효성 검사 실패에 대한 정보 로그에 출력
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                log.info("Field: " + error.getField() + ", Error: " + error.getDefaultMessage());
            }
        }
    }

    @Test
    @WithMockUser(username = "testUser", password = "testPassword")
    @DisplayName("[Board] [Controller] readAllTest")
    void readAllTest() throws Exception {
        // Given
        List<BoardImageDTO> boardImage1 = new ArrayList<>();
        BoardImageDTO imageDTO1 = BoardImageDTO.builder().uuid("uuid1").fileName("image1.png").ord(1).build();
        BoardImageDTO imageDTO2 = BoardImageDTO.builder().uuid("uuid2").fileName("image2.png").ord(2).build();
        boardImage1.add(imageDTO1);
        boardImage1.add(imageDTO2);

        List<BoardImageDTO> boardImage2 = new ArrayList<>();
        BoardImageDTO imageDTO3 = BoardImageDTO.builder().uuid("uuid3").fileName("image3.png").ord(3).build();
        BoardImageDTO imageDTO4 = BoardImageDTO.builder().uuid("uuid4").fileName("image4.png").ord(4).build();
        boardImage2.add(imageDTO3);
        boardImage2.add(imageDTO4);

        List<BoardListAllDTO> mockBoardList = Arrays.asList(
                BoardListAllDTO.builder().title("title1").content("content1").boardImages(boardImage1).build(),
                BoardListAllDTO.builder().title("title2").content("content2").boardImages(boardImage2).build()
        );

        when(boardService.listWithAll()).thenReturn(mockBoardList);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boards/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.boards[0].title").value("title1"))
                .andExpect(jsonPath("$.boards[0].content").value("content1"))
                .andExpect(jsonPath("$.boards[0].boardImages[0].uuid").value("uuid1"))
                .andExpect(jsonPath("$.boards[0].boardImages[0].fileName").value("image1.png"))
                .andExpect(jsonPath("$.boards[0].boardImages[0].ord").value(1))
                .andExpect(jsonPath("$.boards[0].boardImages[1].uuid").value("uuid2"))
                .andExpect(jsonPath("$.boards[0].boardImages[1].fileName").value("image2.png"))
                .andExpect(jsonPath("$.boards[0].boardImages[1].ord").value(2))
                .andExpect(jsonPath("$.boards[1].title").value("title2"))
                .andExpect(jsonPath("$.boards[1].content").value("content2"))
                .andExpect(jsonPath("$.boards[1].boardImages[0].uuid").value("uuid3"))
                .andExpect(jsonPath("$.boards[1].boardImages[0].fileName").value("image3.png"))
                .andExpect(jsonPath("$.boards[1].boardImages[0].ord").value(3))
                .andExpect(jsonPath("$.boards[1].boardImages[1].uuid").value("uuid4"))
                .andExpect(jsonPath("$.boards[1].boardImages[1].fileName").value("image4.png"))
                .andExpect(jsonPath("$.boards[1].boardImages[1].ord").value(4))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser", password = "testPassword")
    @DisplayName("[Board] [Controller] readTest")
    void readTest() throws Exception {
        // Given
        Long boardId = 1L;
        BoardResponseDTO mockBoardResponseDTO = BoardResponseDTO.builder()
                .title("Mock Title")
                .content("Mock Content")
                .build();

        when(boardService.read(boardId)).thenReturn(mockBoardResponseDTO);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boards/{id}", boardId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.board.title").value("Mock Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.board.content").value("Mock Content"))
                .andDo(print());
    }
    @Test
    @WithMockUser(username = "testUser", password = "testPassword")
    @DisplayName("[Board] [Controller] modifyTest")
    void modifyTest() throws Exception {
        // Given
        Long boardId = 1L;
        String updatedTitle = "Updated Title";
        String updatedContent = "Updated Content";

        BoardRequestDTO requestDTO = BoardRequestDTO.builder()
                .title(updatedTitle)
                .content(updatedContent)
                .build();

        String username = "testUser";
        String password = "testPassword";
        // 가상 사용자를 생성하는 부분을 업데이트
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.setContext(securityContext);

        // MemberDetails가 null이라는데?
        MemberDetailsImpl mockMemberDetails = new MemberDetailsImpl(Member.builder()
                .username(username)
                .password(password)
                .build());

        doNothing().when(boardService).modify(boardId, requestDTO, mockMemberDetails);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/boards/{id}", boardId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    // 403 -> with(csrf())
    // 401 -> unauthorizedMember
    // 400 -> MemberDetails

    @Test
    @WithMockUser(username = "testUser", password = "testPassword")
    @DisplayName("[Board] [Controller] deleteTest")
    public void deleteTest(){
        // Given
        Long boardId = 1L;

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

        doNothing().when(boardService).delete(boardId, mockMemberDetails);

        // When, Then
        try {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/boards/{id}", boardId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
        } catch (Exception e) {
            new Exception("error");
        }
    }
}
