package com.pjh.newsfeedtest.board.dto;

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
public class BoardResponseDTO {
    private String title;
    private String content;
    private Long userId;
    private List<String> fileNames;
    private LocalDateTime createdAt;
}
