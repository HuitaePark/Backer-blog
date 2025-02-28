package com.baki.backer.domain.member.dto;

import com.baki.backer.domain.post.Post;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponseDto(
        Long postId,
        String title,
        String content,
        String category,
        LocalDateTime createdDate
) {
    public static PostResponseDto of(Post post) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory_id().name())
                .createdDate(post.getCreate_Date())
                .build();
    }
}