package com.baki.backer.domain.post.repository;


import com.baki.backer.domain.member.QMember;
import com.baki.backer.domain.post.Category;

import com.baki.backer.domain.post.QPost;
import com.baki.backer.domain.post.dto.PostListResponseDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;


import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


import java.util.ArrayList;
import java.util.List;


@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public CustomPostRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PostListResponseDto> searchPost(String keyword, String category, Pageable pageable) {
        QPost post = QPost.post;
        QMember member = QMember.member;
        List<PostListResponseDto> content = jpaQueryFactory
                .select(Projections.constructor(PostListResponseDto.class,
                        post.create_Date,
                        post.post_id,
                        member.name,
                        post.title,
                        post.content))
                .from(post)
                .where(
                        containsKeyword(keyword),
                        matchesCategory(category)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort(),post))
                .fetch();

        long total = jpaQueryFactory
                .selectFrom(post)
                .where(
                        containsKeyword(keyword),
                        matchesCategory(category)
                )
                .fetchCount();
        return PageableExecutionUtils.getPage(content,pageable, () -> total);
    }


    private BooleanExpression containsKeyword(String keyword){
        if(!StringUtils.hasText(keyword)){
            return null;
        }
        QPost post = QPost.post;
        return post.title.containsIgnoreCase(keyword)
                .or(post.content.containsIgnoreCase(keyword))
                .or(post.member.name.containsIgnoreCase(keyword));
    }
    private BooleanExpression matchesCategory(String category) {
        if (!StringUtils.hasText(category)) {
            return null;
        }
        QPost post = QPost.post;
        try {
            Category categoryEnum = Category.valueOf(Integer.parseInt(category));
            return post.category_id.eq(categoryEnum);
        } catch (IllegalArgumentException e) {
            // Handle invalid category value, possibly log the error
            return null;
        }
        }
    private OrderSpecifier<?>[] getOrderSpecifier(org.springframework.data.domain.Sort sort, QPost post) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        sort.forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            switch (order.getProperty()) {
                case "create_date":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, post.create_Date));
                    break;
                case "title":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, post.title));
                    break;
                // Add more cases as needed
                default:
                    // Default sorting
                    orderSpecifiers.add(new OrderSpecifier<>(direction, post.create_Date));
            }
        });

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
    }