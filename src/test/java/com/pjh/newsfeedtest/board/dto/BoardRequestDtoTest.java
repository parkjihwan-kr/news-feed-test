package com.pjh.newsfeedtest.board.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardRequestDtoTest {
    @Test
    @DisplayName("[Board] [domain] [BoardRequestDTO] Create")
    public void CreateBoardRequestDTO(){
        // given
        String title = "title";
        String content = "content";
        List<String> fileNames = Arrays.asList("file1.png","file2.png");

        // when
        BoardRequestDTO boardRequestDTO = BoardRequestDTO.builder()
                .title(title)
                .content(content)
                .fileNames(fileNames)
                .build();

        // then
        assertEquals(title, boardRequestDTO.getTitle());
        assertEquals(content, boardRequestDTO.getContent());
        assertEquals(fileNames, boardRequestDTO.getFileNames());
    }
}
