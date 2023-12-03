package com.pjh.newsfeedtest.board.service;

import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.board.dto.BoardListAllDTO;
import com.pjh.newsfeedtest.board.dto.BoardRequestDTO;
import com.pjh.newsfeedtest.board.dto.BoardResponseDTO;
import com.pjh.newsfeedtest.board.repository.BoardRepository;
import com.pjh.newsfeedtest.file.domain.BoardImage;
import com.pjh.newsfeedtest.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("[Board] [Service] [BoardService] register success")
    public void registerSuccess(){
        // when
        String memberUsername = "username";
        String memberPassword = "encodedPassword";
        String memberContent = "biograpy";

        Member member = Member.builder()
                .username(memberUsername)
                .password(memberPassword)
                .content(memberContent)
                .build();

        String boardTitle = "title";
        String boardContent = "my Content";

        BoardRequestDTO boardRequestDTO = BoardRequestDTO.builder()
                .title(boardTitle)
                .content(boardContent)
                .build();

        BoardImage boardImage = BoardImage.builder()
                .uuid("UUID")
                .fileName("file1.png")
                .ord(1)
                .build();

        HashSet<BoardImage> boardImages = new HashSet<>();
        boardImages.add(boardImage);

        Board board = Board.builder()
                .title(boardRequestDTO.getTitle())
                .content(boardRequestDTO.getContent())
                .member(member)
                .imageSet(boardImages)
                .build();
        // 해당 과정은 등록 과정이기에 createMockBoard() 사용하여 작성하지 않음.

        // when
        boardService.register(boardRequestDTO, member);

        // then
        assertEquals(memberUsername, board.getMember().getUsername());
        assertEquals(memberContent, board.getMember().getContent());
        assertEquals(memberPassword, board.getMember().getPassword());
        assertEquals(boardTitle, board.getTitle());
        assertEquals(boardContent, board.getContent());

    }

    @Test
    @DisplayName("[Board] [Service] [BoardService] read success")
    public void readSuccess(){
        // when
        Board board = createMockBoard();
        when(boardRepository.findByIdWithImages(board.getId())).thenReturn(Optional.of(board));

        // when
        BoardResponseDTO responseDto = boardService.read(board.getId());

        // then
        assertEquals(board.getTitle(), responseDto.getTitle());
        assertEquals(board.getContent(), responseDto.getContent());

        List<String> expectedFileNames = board.getImageSet().stream()
                .sorted(Comparator.comparing(BoardImage::getOrd))
                .map(boardImage -> boardImage.getUuid() + "_" + boardImage.getFileName())
                .collect(Collectors.toList());

        assertEquals(expectedFileNames, responseDto.getFileNames());
    }

    @Test
    @DisplayName("[Board] [Service] [BoardService] read not found BoardId ")
    public void readNotFoundBoardId(){
        // given
        long errorBoardId = 999L;
        when(boardRepository.findByIdWithImages(errorBoardId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(NoSuchElementException.class, () -> boardService.read(errorBoardId));
    }

    @Test
    @DisplayName("[Board] [Service] [BoardService] modify success")
    public void modifySuccess(){
        Board board = createMockBoard();
        // 해당 Service에서 구현하고 있는 change()는 title, content만 바꾸는 내용임으로 변경
        // 해당 board는 boardRepository에 존재하는 board로 가져온다는 가정

        BoardRequestDTO boardRequestDTO = BoardRequestDTO.builder()
                .title("modifyTitle")
                .content("modifyContent")
                .build();

        // 해당 내용을 수정하기 위해 BoardRequestDto를 사용하여 가져옴
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        // when
        boardService.modify(board.getId(), boardRequestDTO, board.getMember());

        // then
        assertEquals(boardRequestDTO.getTitle(), board.getTitle());
        assertEquals(boardRequestDTO.getContent(), board.getContent());
    }

    @Test
    @DisplayName("[Board] [Service] [BoardService] modify unauthorizedUsername")
    public void modifyUnauthorizedUsername(){
        // given
        BoardRequestDTO boardRequestDTO = BoardRequestDTO.builder()
                .title("modifyTitle")
                .content("modifyContent")
                .build();
        // 해당 내용을 수정하기 위해 BoardRequestDto를 사용하여 가져옴

        Member unauthorizedMember = Member.builder()
                .username("unauthorizedUsername")
                .password("encodedPassword")
                .content("biography")
                .build();

        Board board = Board.builder()
                .title(boardRequestDTO.getTitle())
                .content(boardRequestDTO.getContent())
                .member(Member.builder().username("authorizedUsername").build()).build();

        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> boardService.modify(board.getId(), boardRequestDTO, unauthorizedMember));

        // Then
        assertEquals("작성자만 자신의 게시물을 수정할 수 있습니다.", exception.getMessage());
        verify(boardRepository, never()).save(any()); // 저장이 일어나지 않아야 합니다.
    }

    /*@Test
    @DisplayName("[Board] [Service] [BoardService] modify success, include filename")
    public void modifySuccessIncludeFile(){
        // Given
        Board board = createMockBoard();

        List<String> imageFileNames = new ArrayList<>();
        imageFileNames.add("modifyFile1.png");

        BoardRequestDTO boardRequestDTO = BoardRequestDTO.builder()
                .title("title")
                .content("content")
                .fileNames(imageFileNames)
                .build();

        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        // when
        boardService.modify(board.getId(), boardRequestDTO, board.getMember());

        // then
        assertEquals(boardRequestDTO.getTitle(), board.getTitle());
        assertEquals(boardRequestDTO.getContent(), board.getContent());

        List<String> actualFileNames = board.getImageSet().stream()
                .map(boardImage -> boardImage.getUuid() + "_" + boardImage.getFileName())
                .collect(Collectors.toList());

        assertEquals(imageFileNames, actualFileNames);
    }*/

    @Test
    @DisplayName("[Board] [Service] [BoardService] delete success")
    public void deleteSuccess() {
        // given
        Board board = createMockBoard();

        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        // When
        boardService.delete(board.getId(), board.getMember());

        // Then
        verify(boardRepository, times(1)).deleteById(board.getId());
    }

    @Test
    @DisplayName("[Board] [Service] [BoardService] delete unauthorizedUsername")
    public void deleteUnauthorizedUsername() {
        // Given
        Board board = Board.builder()
                .member(Member.builder().username("authorizedUsername").build()).build();
        Member unauthorizedMember = Member.builder()
                .username("unauthorizedUsername")
                .build();

        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> boardService.delete(board.getId(), unauthorizedMember));

        // Then
        assertEquals("자신의 게시물만 삭제할 수 있습니다.", exception.getMessage());
        verify(boardRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("[Board] [Service] [BoardService] listWithAll success")
    public void listWithAllSuccess() {
        // Given
        Board board1 = Board.builder()
                .title("title1")
                .content("content1")
                .member(Member.builder().username("user1").build())
                .build();

        Board board2 = Board.builder()
                .title("title2")
                .content("content2")
                .member(Member.builder().username("user2").build())
                .build();

        when(boardRepository.findAllByOrderByIdDesc()).thenReturn(Arrays.asList(board1, board2));

        // When
        List<BoardListAllDTO> result = boardService.listWithAll();

        // Then
        assertEquals(2, result.size());

        // Check the first board
        BoardListAllDTO boardDTO1 = result.get(0);
        assertEquals("title1", boardDTO1.getTitle());
        assertEquals("content1", boardDTO1.getContent());
        assertEquals("user1", boardDTO1.getWriter());

        // Check the second board
        BoardListAllDTO boardDTO2 = result.get(1);
        assertEquals("title2", boardDTO2.getTitle());
        assertEquals("content2", boardDTO2.getContent());
        assertEquals("user2", boardDTO2.getWriter());
    }

    private Board createMockBoard(){
        String memberUsername = "username";
        String memberPassword = "encodedPassword";
        String memberContent = "biograpy";

        Member member = Member.builder()
                .username(memberUsername)
                .password(memberPassword)
                .content(memberContent)
                .build();

        String boardTitle = "title";
        String boardContent = "my Content";

        BoardImage boardImage = BoardImage.builder()
                .uuid("UUID")
                .fileName("file1.png")
                .ord(1)
                .build();
        // UUID는 임의의 값을 넣어주는 것이기에 해당 값을 UUID.randomUUID().toString()으로 넣어준다면
        // 해당 값에 대한 비교를 해도 같지 않을 수 있기에 passwordEncoder와 마찬가지로 해당값을 uuid화된 값이라
        // 가정하고 "uuid"를 넣는다.

        HashSet<BoardImage> boardImages = new HashSet<>();
        boardImages.add(boardImage);

        Board board = Board.builder()
                .title(boardTitle)
                .content(boardContent)
                .member(member)
                .imageSet(boardImages)
                .build();

        return board;
    }
}