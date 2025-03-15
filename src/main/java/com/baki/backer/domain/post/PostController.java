package com.baki.backer.domain.post;

import com.baki.backer.domain.auth.AuthService;
import com.baki.backer.domain.image.ImageService;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.post.dto.DetailPostResponseDto;
import com.baki.backer.domain.post.dto.PostListResponseDto;
import com.baki.backer.domain.post.dto.PostSaveRequestDto;
import com.baki.backer.global.common.ApiResponseDto;
import com.baki.backer.global.common.SuccessMessageDto;
import com.baki.backer.global.error.ErrorResponse;
import com.baki.backer.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
public class PostController {

    private final PostService postService;
    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    /**
     * 게시물 등록 (이미지 업로드 포함) POST /post consumes 멀티파트 데이터를 받도록 설정
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<?>> posting(
            @RequestPart("post") @Valid PostSaveRequestDto postSaveRequestDto,
            BindingResult bindingResult,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
        String currentUsername = authService.getCurrentSessionUsername(request);

        // BindingResult에 에러가 있다면 에러 응답 반환
        if (bindingResult.hasErrors()) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, "유효성 검증 실패");
            return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
        }

        // 현재 사용자 id 조회
        Long userId = memberRepository.findIdByUsername(currentUsername);

        // 게시물 생성: 여기서 새 게시물의 id를 반환하도록 구현
        Long postId = postService.createPost(postSaveRequestDto, userId);

        // 이미지 파일이 첨부되었다면 업로드 진행
        if (file != null && !file.isEmpty()) {
            try {
                // postService.createPost() 후에 생성된 게시물의 id를 활용하여 이미지 업로드
                Long imageId = imageService.uploadPostImage(file, postId);
                // 필요한 경우 이미지 id를 게시물과 연관짓는 로직 추가
                log.info("게시물 이미지 업로드 성공. 이미지 id: {}", imageId);
            } catch (Exception e) {
                log.error("게시물 이미지 업로드 실패", e);
                // 이미지 업로드 실패 시 롤백 혹은 별도 처리
                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR,
                        "게시물 이미지 업로드 실패: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtil.error(errorResponse));
            }
        }

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
