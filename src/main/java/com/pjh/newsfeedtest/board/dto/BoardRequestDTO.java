package com.pjh.newsfeedtest.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequestDTO {
    @NotBlank
    private String title;
    @NotNull
    private String content;
    private List<String> fileNames;

}
