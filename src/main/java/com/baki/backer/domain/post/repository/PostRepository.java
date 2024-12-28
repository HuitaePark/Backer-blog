package com.baki.backer.domain.post.repository;

import com.baki.backer.domain.post.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer>,CustomPostRepository {
    @Query("SELECT p FROM Post p LEFT JOIN  fetch p.comments where p.id = :post_id")
    Post findPostWithComment(@Param("post_id") Integer post_id);
}

