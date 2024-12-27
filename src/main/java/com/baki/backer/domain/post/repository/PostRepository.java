package com.baki.backer.domain.post.repository;

import com.baki.backer.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer>,CustomPostRepository {

}

