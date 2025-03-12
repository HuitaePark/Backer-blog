package com.baki.backer.domain.post;

import com.baki.backer.domain.auth.AuthService;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.post.dto.DetailPostResponseDto;
import com.baki.backer.domain.post.dto.PostListResponseDto;
import com.baki.backer.domain.post.dto.PostSaveRequestDto;
import com.baki.backer.global.common.SuccessMessageDto;
import com.baki.backer.global.common.ApiResponseDto;
import com.baki.backer.global.error.ErrorResponse;
import com.baki.backer.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostServiceImpl postService;
    private final AuthService authService;
    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<ApiResponseDto<?>> posting(
            @Valid @RequestBody PostSaveRequestDto postSaveRequestDto,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        String currentUsername = authService.getCurrentSessionUsername(request);


        // BindingResult에 에러가 있다면 에러 응답 반환
        if (bindingResult.hasErrors()) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생하였습니다.");
            return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
        }

        //리포지토리를 컨트롤러가 의존해도 될까?
        Long userId = memberRepository.findIdByUsername(currentUsername);

        postService.createPost(postSaveRequestDto, userId);
        SuccessMessageDto message = new SuccessMessageDto(200, "게시물 작성을 성공하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.ok(message));
    }

    @PatchMapping("/{post_id}")
    public ResponseEntity<ApiResponseDto<?>> updating(
            @Valid @RequestBody PostSaveRequestDto requestDto,
            BindingResult bindingResult,
            HttpServletRequest request,
            @PathVariable Long post_id
    ) {
        String currentUsername = authService.getCurrentSessionUsername(request);

        // 다른 유저가 수정할 경우
        if (postService.checkWriterEquals(currentUsername, post_id)) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.error(errorResponse));
        }

        // 기타 에러
        if (bindingResult.hasErrors()) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생하였습니다.");
            return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
        }

        postService.updatePost(post_id, requestDto);
        SuccessMessageDto message = new SuccessMessageDto(200, "게시물 수정을 성공하였습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.ok(message));
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<ApiResponseDto<?>> removing(
            @PathVariable Long post_id,
            HttpServletRequest request
    ) {
        String currentUsername = authService.getCurrentSessionUsername(request);


        // 다른 유저가 삭제할 경우
        if (postService.checkWriterEquals(currentUsername, post_id)) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.error(errorResponse));
        }

        postService.deletePost(post_id);
        SuccessMessageDto message = new SuccessMessageDto(200, "게시물 삭제를 성공하였습니다.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtil.ok(message));
    }

    @GetMapping("/view/{post_id}")
    public ResponseEntity<ApiResponseDto<DetailPostResponseDto>> getPost(
            @PathVariable Long post_id,
            HttpServletRequest request
    ) {
        DetailPostResponseDto responseDto = postService.getPostInfo(post_id);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.ok(responseDto));
    }

    /**
     * 게시물을 검색하고 페이징 처리된 결과를 반환합니다.
     *
     * @param keyword  검색 키워드 (옵션)
     * @param category 게시물 카테고리 ID (옵션)
     * @param page     페이지 번호 (기본값: 1)
     * @param size     페이지 크기 (기본값: 20)
     * @param sort     정렬 기준 (예: "create_date,desc")
     * @return 페이징 처리된 게시물 목록
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponseDto<Page<PostListResponseDto>>> getPostList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "create_Date,desc") String sort
    ) {
        Page<PostListResponseDto> posts = postService.getPostList(keyword, category, page, size, sort);
        return ResponseEntity.ok(ResponseUtil.ok(posts));
    }
}
