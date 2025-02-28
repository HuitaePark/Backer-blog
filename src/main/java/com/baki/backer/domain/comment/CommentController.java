package com.baki.backer.domain.comment;

import com.baki.backer.domain.auth.AuthService;
import com.baki.backer.domain.comment.dto.CommentRequestDto;
import com.baki.backer.domain.comment.dto.CommentResponseDto;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.post.Post;
import com.baki.backer.domain.post.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping  // 일반적으로 컨트롤러 레벨에서 RequestMapping 지정
public class CommentController {

    private final CommentService commentService;
    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentController(CommentService commentService,
                             AuthService authService,
                             MemberRepository memberRepository,
                             PostRepository postRepository) {
        this.commentService = commentService;
        this.authService = authService;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }
    @GetMapping("/post/{post_id}/comments")
    public List<CommentResponseDto> findAllComment(@PathVariable Long post_id){
        return commentService.getAllComment(post_id);
    }
    /**
     * 댓글 생성
     * POST /comment
     */
    @PostMapping("/comment")
    public ResponseEntity<?> createComment(
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        // 1) DTO 유효성 검사
        if (bindingResult.hasErrors()) {
            // 검증 에러가 있다면 에러 메시지 반환
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // 2) 현재 세션에서 사용자명(username) 꺼내고, DB에서 userId를 찾는다
        String currentUsername = authService.getCurrentSessionUsername(request);
        Long userId = memberRepository.findIdByUsername(currentUsername);

        // 3) 댓글이 달릴 게시글 ID를 DTO에서 가져온다고 가정
        Long postId = commentRequestDto.getPostId();
        if (postId == null) {
            return ResponseEntity.badRequest().body("postId가 누락되었습니다.");
        }

        // 4) 서비스 호출 (존재하지 않는 회원/게시글이면 서비스에서 예외 처리)
        commentService.createComment(commentRequestDto, userId, postId);

        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 작성을 성공하였습니다.");
    }

    /**
     * 댓글 수정
     * PATCH /comment/{comment_id}
     */
    @PatchMapping("/{comment_id}")
    public ResponseEntity<?> updateComment(
            @PathVariable("comment_id") Long commentId,
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        // 1) DTO 유효성 검사
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // 2) 현재 사용자 확인 (권한 체크가 필요하면 여기서 수행)
        String currentUsername = authService.getCurrentSessionUsername(request);
        Long userId = memberRepository.findIdByUsername(currentUsername);

        // 3) 서비스 로직 호출
        //    - 현재 구현된 서비스 메서드는 (Long commentId, String newContent)를 받음
        commentService.updateComment(commentId, commentRequestDto.getContent());

        return ResponseEntity.ok("댓글 수정에 성공하였습니다.");
    }

    /**
     * 댓글 삭제
     * DELETE /comment/{comment_id}
     */
    @DeleteMapping("/{comment_id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("comment_id") Long commentId,
            HttpServletRequest request
    ) {
        // 1) 현재 사용자 확인 (권한 체크가 필요하면 여기서 수행)
        String currentUsername = authService.getCurrentSessionUsername(request);
        Long userId = memberRepository.findIdByUsername(currentUsername);

        // 2) 댓글 먼저 찾아본다 (컨트롤러에서 직접)
        Comment comment = commentService.findCommentById(commentId);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글이 존재하지 않습니다.");
        }

        // 3) 댓글에 연결된 게시글이 있는지 확인
        Post post = comment.getPost();
        if (post == null) {
            return ResponseEntity.badRequest().body("댓글에 연결된 게시글이 없습니다.");
        }

        // 4) 게시글이 실제로 존재하는지 확인
        //    - DB FK 제약이 없으므로, 혹시라도 postId가 무효일 수 있어서 확인
        if (!postRepository.existsById(post.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 이미 삭제되었거나 존재하지 않습니다.");
        }

        // (추가) 권한 로직: comment.getMember().getId() 와 userId 비교해서, 다르면 예외 등

        // 5) 서비스에서 삭제 처리
        commentService.deleteComment(commentId);

        return ResponseEntity.ok("댓글 삭제를 성공하였습니다.");
    }
}
