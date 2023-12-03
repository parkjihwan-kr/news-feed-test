package com.pjh.newsfeedtest.board.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BoardResponseDtoTest {
    @Test
    @DisplayName("[Board] [Dto] [BoardResponseDto] [Create]")
    public void createBoardResponseDtoTest(){
        // given
        String title = "title";
        String content = "content";
        List<String> fileNames = Arrays.asList("file.png");
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        BoardResponseDTO boardResponseDTO = BoardResponseDTO.builder()
                .title(title)
                .content(content)
                .fileNames(fileNames)
                .createdAt(createdAt)
                .build();

        //then
        assertEquals(title, boardResponseDTO.getTitle());
        assertEquals(content, boardResponseDTO.getContent());
        assertEquals(fileNames, boardResponseDTO.getFileNames());
        assertEquals(createdAt, boardResponseDTO.getCreatedAt());
    }

    @Test
    @DisplayName("[Board] [domain] [BoardResponseDTO] [invalidateField]")
    public void invalidateTitle(){
        // given
        String title = "null";
        String content = null;
        List<String> fileNames = Arrays.asList("file.png","test.png");

        BoardResponseDTO boardResponseDTO = BoardResponseDTO.builder()
                .title(title)
                .content(content)
                .fileNames(fileNames).build();
        //when & then
        assertThrows(java.lang.NullPointerException.class, ()-> invalidateBoardResponseDTO(boardResponseDTO));
    }

    public void invalidateBoardResponseDTO(BoardResponseDTO boardResponseDTO){
        if(boardResponseDTO.getTitle() == null){
            throw new NullPointerException("제목에 null값이 들어갔습니다.");
        }
        if(boardResponseDTO.getContent() == null){
            throw new NullPointerException("내용에 null값이 들어갔습니다.");
        }
    }
}
