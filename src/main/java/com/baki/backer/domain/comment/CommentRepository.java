package com.baki.backer.domain.comment;

import com.baki.backer.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByMember(Member member);
    List<Comment> findByPostId(Long postId);
}
