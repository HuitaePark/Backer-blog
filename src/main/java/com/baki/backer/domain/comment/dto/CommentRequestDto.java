package com.baki.backer.domain.comment.dto;

import com.baki.backer.domain.comment.Comment;
import com.baki.backer.domain.member.Member;
import lombok.Data;

@Data
public class CommentRequestDto {
    private Long id;
    private String content;
    private Member member;
    public CommentRequestDto(Comment comment) {
        this.id = comment.getComment_id();
        this.content = comment.getContent();
        this.member = comment.getMember();
    }
}
