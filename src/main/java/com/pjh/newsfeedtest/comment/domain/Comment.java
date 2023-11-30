package com.pjh.newsfeedtest.comment.domain;

import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.comment.dto.CommentRequestDto;
import com.pjh.newsfeedtest.domain.BaseEntity;
import com.pjh.newsfeedtest.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 번호

    @Column(nullable = false)
    private String content; // 댓글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    public Comment(Member member, CommentRequestDto commentRequestDto, Board board) {
        this.member = member;
        this.board = board;
        this.content = commentRequestDto.getContent();
    }

    public void update(CommentRequestDto commentRequestDto) {
        this.content = commentRequestDto.getContent();
    }


}
