package com.baki.backer.domain.comment;

import com.baki.backer.domain.comment.dto.CommentRequestDto;

public interface CommentService {
    /**
     * 댓글 등록
     *
     *
     */
    void createComment(CommentRequestDto request, Long userId,Long postId);

    /**
     * 댓글 수정
     *
     *
     */
    void updateComment(Long commentId, String newContent);

    /**
     * 댓글 삭제
     */
    void deleteComment(Long commentId);

    Comment findCommentById(Long commentId);
}
