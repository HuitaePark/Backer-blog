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
                .commentId(comment.getComment_id())
                .content(comment.getContent())
                .postId(comment.getPost().getPost_id())
                .build();
    }
}