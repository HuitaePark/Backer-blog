package com.baki.backer.domain.comment;

import com.baki.backer.domain.comment.dto.CommentResponseDto;
import com.baki.backer.domain.member.Member;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByMember(Member member);
    List<Comment> findAllByPostId(Long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId " +
            "AND (:cursor IS NULL OR c.id > :cursor) " +
            "ORDER BY c.id ASC")
    List<Comment> findNextComments(
            @Param("cursor") Long cursor,
            @Param("postId") Long postId,
            Pageable pageable
    );
}
