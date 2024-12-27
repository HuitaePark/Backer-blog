package com.baki.backer.domain.post.repository;

import com.baki.backer.domain.post.Post;

import java.util.List;

public interface CustomPostRepository{
    public List<Post> PostQuery();
}