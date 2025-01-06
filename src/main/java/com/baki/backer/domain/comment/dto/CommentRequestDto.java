package com.baki.backer.domain.comment.dto;

import com.baki.backer.domain.comment.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentRequestDto {
    private Long id;
    private String content;

    // Member 정보
    private Long memberId;

    // Post 정보
    private Long postId;

    public CommentRequestDto(Comment comment) {
        this.id = comment.getComment_id();
        this.content = comment.getContent();

        // LAZY 로딩 문제 방지: service/repository 단에서 fetch join으로 member와 post 미리 로딩
        if (comment.getMember() != null) {
            this.memberId = comment.getMember().getId();
        }

        if (comment.getPost() != null) {
            this.postId = comment.getPost().getPost_id();      // 엔티티에서 post_id 가져오기// 엔티티에서 title 가져오기
        }
    }
}