package com.pjh.newsfeedtest.board.service;

import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.board.dto.BoardListAllDTO;
import com.pjh.newsfeedtest.board.dto.BoardRequestDTO;
import com.pjh.newsfeedtest.board.dto.BoardResponseDTO;
import com.pjh.newsfeedtest.board.repository.BoardRepository;
import com.pjh.newsfeedtest.file.dto.BoardImageDTO;
import com.pjh.newsfeedtest.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;

    public void register(BoardRequestDTO boardRequestDTO, Member member) {
        Board board = dtoToEntity(boardRequestDTO, member);

        boardRepository.save(board);
    }
    @Transactional(readOnly = true)
    public BoardResponseDTO read(Long id) {
        Optional<Board> result = boardRepository.findByIdWithImages(id);
        Board board = result.orElseThrow();

        BoardResponseDTO boardResponseDTO = entityToDTO(board);
        return boardResponseDTO;
    }

    public void modify(Long id, BoardRequestDTO boardRequestDTO, Member member) {
        Optional<Board> result = boardRepository.findById(id);
        Board board = result.orElseThrow();

        if(member.getUsername().equals(board.getMember().getUsername())) {
            board.change(boardRequestDTO.getTitle(), boardRequestDTO.getContent());
            board.clearImages();

            if(boardRequestDTO.getFileNames() != null) {
                for(String fileName : boardRequestDTO.getFileNames()) {
                    String[] arr = fileName.split("_");
                    board.addImage(arr[0], arr[1]);
                }
            }
            boardRepository.save(board);

        } else {
            throw new RuntimeException("작성자만 자신의 게시물을 수정할 수 있습니다.");
        }
    }
    public void delete(Long id, Member member) {
        Optional<Board> result = boardRepository.findById(id);
        Board board = result.orElseThrow();

        if(member.getUsername().equals(board.getMember().getUsername())) {
            boardRepository.deleteById(id);
        } else {
            throw new RuntimeException("자신의 게시물만 삭제할 수 있습니다.");
        }
    }
    @Transactional(readOnly = true)
    public List<BoardListAllDTO> listWithAll() {
        List<BoardListAllDTO> boardListAllDTOList = boardRepository.findAllByOrderByIdDesc().stream().
                map(board -> {
                  BoardListAllDTO boardListAllDTO = BoardListAllDTO.builder()
                          .title(board.getTitle())
                          .content(board.getContent())
                          .createdAt(board.getCreatedAt())
                          .writer(board.getMember().getUsername())
                          .build();
                  List<BoardImageDTO> imageDTOS = board.getImageSet().stream().sorted()
                          .map(boardImage -> BoardImageDTO.builder()
                                  .uuid(boardImage.getUuid())
                                  .fileName(boardImage.getFileName())
                                  .ord(boardImage.getOrd())
                                  .build()).collect(Collectors.toList());
                  boardListAllDTO.setBoardImages(imageDTOS);
                  return boardListAllDTO;
                }).collect(Collectors.toList());
        return  boardListAllDTOList;
    }
    private Board dtoToEntity(BoardRequestDTO boardRequestDTO, Member member) {
        Board board = Board.builder()
                .title(boardRequestDTO.getTitle())
                .content(boardRequestDTO.getContent())
                .member(member)
                .build();

        if(boardRequestDTO.getFileNames() != null) {
            boardRequestDTO.getFileNames().forEach(fileName -> {
                String [] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            });
        }
        return board;
    }
    private BoardResponseDTO entityToDTO(Board board){
        BoardResponseDTO boardResponseDTO = BoardResponseDTO.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getMember().getId())
                .createdAt(board.getCreatedAt())
                .build();

        List<String> fileName = board.getImageSet().stream().sorted().map(boardImage ->
                boardImage.getUuid()+"_"+boardImage.getFileName()).collect(Collectors.toList());
        boardResponseDTO.setFileNames(fileName);
        return boardResponseDTO;
    }
}
