package com.pjh.newsfeedtest.board.dto;

import com.pjh.newsfeedtest.file.dto.BoardImageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardListAllDTO {
    private String title;
    private String writer;
    private String content;
    private LocalDateTime createdAt;
    private List<BoardImageDTO> boardImages;
}
