package com.baki.backer.domain.post.repository;

import com.baki.backer.domain.post.Post;
import com.baki.backer.domain.post.dto.PostListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CustomPostRepository{
    Page<PostListResponseDto> searchPost(String keyword, String category, Pageable pageable);
}