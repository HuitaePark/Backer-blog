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
import org.springframework.data.domain.Sort;
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
                        post.id,
                        member.name,
                        post.title,
                        post.content))
                .from(post)
                .leftJoin(post.member, member)
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
            // Handle invalid category value, possibly log the exception
            return null;
        }
        }
    private OrderSpecifier<?>[] getOrderSpecifier(org.springframework.data.domain.Sort sort, QPost post) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        if (sort.isUnsorted()) {
            // 정렬 조건이 없으면 기본 정렬: 생성일 DESC + id DESC
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, post.create_Date));
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, post.id));
        } else {
            // API에서 전달받은 정렬 조건 적용
            sort.forEach(order -> {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                String property = order.getProperty();
                switch (property) {
                    case "create_Date":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, post.create_Date));
                        break;
                    case "title":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, post.title));
                        break;
                    // 필요한 경우 다른 속성에 대한 케이스 추가
                    default:
                        orderSpecifiers.add(new OrderSpecifier<>(direction, post.create_Date));
                }
            });
            // API 정렬 조건에 id 정렬이 포함되어 있지 않다면 tie-breaker로 id DESC 추가
            boolean containsId = false;
            for (Sort.Order order : sort) {
                if ("id".equals(order.getProperty()) || "post_id".equals(order.getProperty())) {
                    containsId = true;
                    break;
                }
            }
            if (!containsId) {
                orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, post.id));
            }
        }
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

}