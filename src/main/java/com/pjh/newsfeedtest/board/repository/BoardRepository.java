package com.pjh.newsfeedtest.board.repository;

import com.pjh.newsfeedtest.board.domain.Board;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByOrderByIdDesc();
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.id = :id")
    Optional<Board> findByIdWithImages(Long id);

}
