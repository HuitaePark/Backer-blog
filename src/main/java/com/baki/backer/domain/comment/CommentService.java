package com.baki.backer.domain.comment;

import com.baki.backer.domain.comment.dto.CommentRequestDto;
import com.baki.backer.domain.comment.dto.CommentResponseDto;
import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.post.Post;
import com.baki.backer.domain.post.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService{
    MemberRepository memberRepository;
    CommentRepository commentRepository;
    PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, MemberRepository memberRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }


    public List<CommentResponseDto> getComment(Long postId, Long cursor) {
        // 초기 조회 시 cursor가 null이면 0으로 처리
        if (cursor == null) {
            cursor = 0L;
        }

        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = commentRepository.findNextComments(cursor, postId, pageable);

        return convertToDto(comments);
    }
    private List<CommentResponseDto> convertToDto(List<Comment> comments) {
        return comments.stream()
                .map(comment -> {
                    // 멤버가 null이거나 존재하지 않으면 "Unknown" 처리
                    String memberName = comment.getMember() == null ? "Unknown" : Optional.ofNullable(comment.getMember().getName()).orElse("Unknown");

                    return new CommentResponseDto(
                            comment.getId(),
                            comment.getPost() != null ? comment.getPost().getId() : null,
                            comment.getContent(),
                            memberName,
                            comment.getCreateDate()
                    );
                })
                .collect(Collectors.toList());
    }
    /**
     *
     * @param request dto 입력
     * @param userId 유저아이디 검사
     * @param postId 게시물 아이디 검사
     *
     */

    public void createComment(CommentRequestDto request, Long userId,Long postId) {
        // 현재 유저의 Member 엔티티 조회
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .member(member)
                .post(post)
                .content(request.getContent())
                .build();
        commentRepository.save(comment);
    }

    public void updateComment(Long commentId, String newContent) {
        // 1. commentId로 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("해당 댓글이 존재하지 않습니다."));

        // 2. 댓글에 연결된 Post가 있는지 확인
        Post post = comment.getPost();
        if (post == null) {
            throw new IllegalArgumentException("이 댓글은 어떤 게시글에도 연결되어 있지 않습니다.");
        }

        // 3. Post가 실제 DB에 존재하는지(혹은 이미 삭제된 상태인지 등) 확인
        //    - 보통 comment.getPost() 자체가 영속성 컨텍스트에 의해 관리되므로
        //      DB에 존재하지 않을 경우 예외가 터지겠지만, 명시적으로 확인할 수도 있음
        if (!postRepository.existsById(post.getId())) {
            throw new NoSuchElementException("댓글에 연결된 게시글이 존재하지 않습니다.");
        }

        // 4. 수정 로직
        comment.setContent(newContent);
        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        // 1. commentId로 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("해당 댓글이 존재하지 않습니다."));

        // 2. 연결된 Post 검사 (필요에 따라 권한 검사 등 포함)
        if (comment.getPost() == null) {
            throw new IllegalArgumentException("이 댓글은 어떤 게시글에도 연결되어 있지 않습니다.");
        }

        // 3. 필요하면 postId를 가져와서 추가 검증
        Long postId = comment.getPost().getId();
        // 예: 작성자 권한이 맞는지, postId가 특정 범위인지 등등

        // 4. 삭제 로직
        commentRepository.delete(comment);
    }

    public Comment findCommentById(Long commentId) {
        // 찾지 못하면 null 반환 or Optional을 그대로 반환해도 됨
        return commentRepository.findById(commentId).orElse(null);
    }
}
