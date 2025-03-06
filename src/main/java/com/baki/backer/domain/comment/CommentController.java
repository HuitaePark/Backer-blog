package com.baki.backer.domain.comment;

import com.baki.backer.domain.auth.AuthService;
import com.baki.backer.domain.comment.dto.CommentRequestDto;
import com.baki.backer.domain.comment.dto.CommentResponseDto;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.post.Post;
import com.baki.backer.domain.post.repository.PostRepository;
import com.baki.backer.global.common.ApiResponseDto;
import com.baki.backer.global.common.SuccessMessageDto;
import com.baki.backer.global.error.ErrorResponse;
import com.baki.backer.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping  // 일반적으로 컨트롤러 레벨에서 RequestMapping 지정
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
    @GetMapping("/post/{post_id}/comment/")
    public ResponseEntity<ApiResponseDto<List<CommentResponseDto>>> findAllComment(
            @PathVariable("post_id") Long postId,
            @RequestParam(value = "cursor", required = false) Long cursor) {

        List<CommentResponseDto> response = commentService.getComment(postId, cursor);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.ok(response));
    }


    /**
     *
     * @param commentRequestDto
     * @param bindingResult
     * @param request
     * @param post_id
     * @return
     */
    @PostMapping("/post/{post_id}/comment")
    public ResponseEntity<ApiResponseDto<?>> createComment(
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            BindingResult bindingResult,
            HttpServletRequest request,
            @PathVariable Long post_id
    ) {
        // 1) DTO 유효성 검사
        if (bindingResult.hasErrors()) {

            ErrorResponse errorResponse = ErrorResponse.of(bindingResult);
            return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
        }

        // 2) 현재 세션에서 사용자명(username) 꺼내고, DB에서 userId를 찾는다
        String currentUsername = authService.getCurrentSessionUsername(request);
        Long userId = memberRepository.findIdByUsername(currentUsername);

        // 3) 댓글이 달릴 게시글 ID를 DTO에서 가져온다고 가정
        if (post_id == null) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, "postId가 누락되었습니다.");
            return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
        }

// 4) 서비스 호출 (존재하지 않는 회원/게시글이면 서비스에서 예외 처리)
        commentService.createComment(commentRequestDto, userId, post_id);

        SuccessMessageDto successMessage = new SuccessMessageDto(201, "댓글 작성을 성공하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.ok(successMessage));
    }

    /**
     * 댓글 수정
     * PATCH /comment/{comment_id}
     */
    @PatchMapping("/{comment_id}")
    public ResponseEntity<ApiResponseDto<?>> updateComment(
            @PathVariable("comment_id") Long commentId,
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        // 1) DTO 유효성 검사
        if (bindingResult.hasErrors()) {
            ErrorResponse errorResponse = ErrorResponse.of(bindingResult);
            return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
        }

        // 2) 현재 사용자 확인 (권한 체크가 필요하면 여기서 수행)
        String currentUsername = authService.getCurrentSessionUsername(request);
        Long userId = memberRepository.findIdByUsername(currentUsername);

        // 3) 서비스 로직 호출
        //    - 현재 구현된 서비스 메서드는 (Long commentId, String newContent)를 받음
        commentService.updateComment(commentId, commentRequestDto.getContent());

        SuccessMessageDto successMessage = new SuccessMessageDto(200, "댓글 작성을 성공하였습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.ok(successMessage));
    }

    /**
     * 댓글 삭제
     * DELETE /comment/{comment_id}
     */
    @DeleteMapping("/{comment_id}")
    public ResponseEntity<ApiResponseDto<?>> deleteComment(
            @PathVariable("comment_id") Long commentId,
            HttpServletRequest request
    ) {
        // 1) 현재 사용자 확인 (권한 체크가 필요하면 여기서 수행)
        String currentUsername = authService.getCurrentSessionUsername(request);
        Long userId = memberRepository.findIdByUsername(currentUsername);

        // 2) 댓글 먼저 찾아본다 (컨트롤러에서 직접)
        Comment comment = commentService.findCommentById(commentId);
        if (comment == null) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND, "댓글이 없습니다.");
            return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
        }

        // 3) 댓글에 연결된 게시글이 있는지 확인
        Post post = comment.getPost();
        if (post == null) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND, "댓글에 연결된 게시글이 없습니다.");
            return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
        }

        // 4) 게시글이 실제로 존재하는지 확인
        //    - DB FK 제약이 없으므로, 혹시라도 postId가 무효일 수 있어서 확인
        if (!postRepository.existsById(post.getId())) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND, "게시글이 삭제되었거나 이미 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
        }

        // (추가) 권한 로직: comment.getMember().getId() 와 userId 비교해서, 다르면 예외 등

        // 5) 서비스에서 삭제 처리
        commentService.deleteComment(commentId);

        SuccessMessageDto successMessage = new SuccessMessageDto(200, "댓글 삭제를 성공하였습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.ok(successMessage));
    }
}
