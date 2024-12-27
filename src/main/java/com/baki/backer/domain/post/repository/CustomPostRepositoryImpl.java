package com.baki.backer.domain.post.repository;

import com.baki.backer.domain.post.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static com.baki.backer.domain.post.QPost.post;


public class CustomPostRepositoryImpl implements CustomPostRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public CustomPostRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Post> PostQuery() {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .fetch();
    }
}
