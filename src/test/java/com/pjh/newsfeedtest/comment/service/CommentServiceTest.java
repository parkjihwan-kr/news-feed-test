package com.pjh.newsfeedtest.comment.service;

import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.board.repository.BoardRepository;
import com.pjh.newsfeedtest.board.service.BoardService;
import com.pjh.newsfeedtest.comment.domain.Comment;
import com.pjh.newsfeedtest.comment.dto.CommentRequestDto;
import com.pjh.newsfeedtest.comment.dto.CommentResponseDto;
import com.pjh.newsfeedtest.comment.repository.CommentRepository;
import com.pjh.newsfeedtest.member.domain.Member;
import com.pjh.newsfeedtest.member.repository.MemberRepository;
import com.pjh.newsfeedtest.security.service.MemberDetailsImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    BoardRepository boardRepository;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    CommentService commentService;

    @PersistenceContext
    private EntityManager entityManager;

    @InjectMocks
    BoardService boardService;

    @Test
    @DisplayName("[Comment] [Service] createComment Success")
    void createCommentSuccess() {
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .content("comment test")
                .build();
        MemberDetailsImpl loginMember = new MemberDetailsImpl(Member.builder().username("loginUsername").build());

        Board board = Board.builder()
                .id(1L)
                .title("title1")
                .content("content1")
                .member(loginMember.getMember())
                .build();
        // 게시판에 작성하는 Member는 MemberDetails에 존재하는 Member이기에

        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));
        // 해당 board에 게시글이 있어야 comment를 작성할 수 있기에

        // when
        ResponseEntity<CommentResponseDto> responseDto = commentService.createComment(board.getId(), requestDto, loginMember.getMember());

        // Then
        assertEquals(HttpStatus.OK, responseDto.getStatusCode());
        assertEquals(requestDto.getContent(),responseDto.getBody().getContent());
        assertEquals(loginMember.getMember().getUsername(),responseDto.getBody().getUsername());
        assertNotNull(responseDto.getBody());
    }

    @Test
    @DisplayName("[Comment] [Service] board not found")
    public void createCommentNotFoundBoard(){
        // given
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .content("comment test")
                .build();
        MemberDetailsImpl loginMember = new MemberDetailsImpl(Member.builder().username("loginUsername").build());

        Board board = Board.builder()
                .title("Test Title")
                .content("Test Content")
                .build();

        boardRepository.save(board);

        // when, then
        assertThrows(java.lang.IllegalArgumentException.class, ()->commentService.createComment(board.getId(),requestDto, loginMember.getMember()));
    }

    @Test
    @DisplayName("[Comment] [Service] updateComment")
    public void updateCommentSuccess(){
        // Long commentId, CommentRequestDto commentRequestDto, Member member
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .content("comment update!")
                .build();

        MemberDetailsImpl loginMember = new MemberDetailsImpl(Member.builder().username("loginMemberUsername").build());

        Member boardMember = Member.builder()
                .username(loginMember.getUsername())
                .content("content1")
                .build();
        // boardMember와 loginMember는 같아야 성공 시나리오 가능하기에

        Comment comment = Comment.builder()
                .id(1L)
                .content("existing Comment")
                .member(boardMember)
                .build();

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // when
        ResponseEntity<?> responseEntity = commentService.updateComment(comment.getId(), requestDto, loginMember.getMember());

        // then
        verify(commentRepository, times(1)).findById(comment.getId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("[Comment] [Service] updateComment Not found comment")
    public void updateCommentNotFoundComment(){
        // given
        long commentId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto("update comment");
        MemberDetailsImpl loginMember = new MemberDetailsImpl(Member.builder().username("loginMemberUsername").build());

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when, then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(commentId, requestDto, loginMember.getMember()));

        assertEquals("댓글이 존재하지 않습니다.", exception.getMessage());

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("[Comment] [Service] [CommentService] updateComment Unauthorized")
    void updateCommentUnauthorized() {
        // given
        CommentRequestDto requestDto = CommentRequestDto.builder().content("updated content").build();
        MemberDetailsImpl loginMember = new MemberDetailsImpl(Member.builder().username("loginMemberUsername").build());
        Member unauthorizedMember = Member.builder().username("unauthorizedUser").build();

        Comment comment = Comment.builder()
                .id(1L)
                .member(unauthorizedMember)
                .content("comment1")
                .build();

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // when
        ResponseEntity<?> response = commentService.updateComment(comment.getId(), requestDto, loginMember.getMember());

        // then
        verify(commentRepository, times(1)).findById(comment.getId());
        verify(commentRepository, never()).save(any(Comment.class));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("작성자만 수정할 수 있습니다.", response.getBody());
    }

    @Test
    @DisplayName("[Comment] [Service] deleteComment success")
    public void deleteCommentSuccess(){
        MemberDetailsImpl loginMember = new MemberDetailsImpl(Member.builder().username("loginMemberUsername").build());
        Board board = Board.builder()
                .id(1L)
                .title("title")
                .content("content1")
                .member(loginMember.getMember())
                .build();
        // board는 loginMember가 작성한 게시물

        Comment comment = Comment.builder()
                .id(1L)
                .member(loginMember.getMember())
                .board(board)
                .content("comment1")
                .build();
        // 해당 댓글은 로그인한 사용자가 작성한 댓글

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // when
        ResponseEntity<?> responseEntity = commentService.deleteComment(comment.getId(), loginMember.getMember());

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("댓글을 삭제하였습니다.", responseEntity.getBody());
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    @DisplayName("[Comment] [Service] [CommentService] deleteComment Unauthorized")
    void deleteCommentUnauthorized() {

        // given
        Member boardMember = Member.builder()
                .id(1L)
                .username("unauthorizedMember")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .member(boardMember)
                .build();

        MemberDetailsImpl loginMember = new MemberDetailsImpl(Member.builder().username("loginMemberUsername").build());

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        // when
        ResponseEntity<?> responseEntity = commentService.deleteComment(comment.getId(), loginMember.getMember());
        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("작성자만 삭제할 수 있습니다.", responseEntity.getBody());
        verify(commentRepository, never()).delete(comment);
    }

    @Test
    @DisplayName("[Comment] [Service] [CommentService] deleteComment CommentNotFound")
    void deleteCommentNotFound() {
        // given
        long commentId = 1L;
        MemberDetailsImpl loginMember = new MemberDetailsImpl(Member.builder().username("loginMemberUsername").build());

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when, then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteComment(commentId, loginMember.getMember()));

        // then
        assertEquals("댓글이 존재하지 않습니다.", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentRepository, never()).delete(any());
    }
}
