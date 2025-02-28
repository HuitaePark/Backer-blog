package com.baki.backer.domain.member.dto;

import com.baki.backer.domain.comment.Comment;
import lombok.Builder;

@Builder
public record CommentResponseDto(
        Long commentId,
        String content,
        Long postId
) {
    public static CommentResponseDto of(Comment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .build();
    }
}