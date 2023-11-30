package com.pjh.newsfeedtest.comment.repository;


import com.pjh.newsfeedtest.board.domain.Board;
import com.pjh.newsfeedtest.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardOrderByCreatedAtDesc(Board board); // 작성일 기준 내림차순 정렬

    void deleteByBoard_Id(Long id);

}
