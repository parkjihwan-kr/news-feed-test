package com.pjh.newsfeed.board.dto;

import com.pjh.newsfeedtest.board.dto.BoardListAllDTO;
import com.pjh.newsfeedtest.file.domain.BoardImage;
import com.pjh.newsfeedtest.file.dto.BoardImageDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardListAllDtoTest {
    @Test
    @DisplayName("[Board] [domain] [BoardListAllDto] Create")
    public void createBoardListAllDto(){
        // given
        String title = "title";
        String writer = "writer";
        String content = "content";
        LocalDateTime createdAt = LocalDateTime.now();
        List<BoardImageDTO> boardImages = Arrays.asList(new BoardImageDTO(UUID.randomUUID().toString(), "file.png", 1));

        // when
        BoardListAllDTO boardListAllDTO = BoardListAllDTO.builder()
                .title(title)
                .writer(writer)
                .content(content)
                .createdAt(createdAt)
                .boardImages(boardImages)
                .build();

        // then
        assertEquals(title, boardListAllDTO.getTitle());
        assertEquals(writer, boardListAllDTO.getWriter());
        assertEquals(content, boardListAllDTO.getContent());
        assertEquals(createdAt, boardListAllDTO.getCreatedAt());
        assertEquals(boardImages, boardListAllDTO.getBoardImages());
    }
}
