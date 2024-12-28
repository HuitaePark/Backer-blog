package com.baki.backer.domain.comment.dto;

import com.baki.backer.domain.comment.Comment;

public class CommentRequestDto {
    private Integer id;
    private String content;

    public CommentRequestDto(Comment comment) {
        this.id = comment.getComment_id();
        this.content = comment.getContent();
    }
}
