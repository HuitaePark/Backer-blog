package com.baki.backer.domain.post;

import com.baki.backer.domain.auth.AuthService;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.post.dto.DetailPostResponseDto;
import com.baki.backer.domain.post.dto.PostListResponseDto;
import com.baki.backer.domain.post.dto.PostSaveRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostServiceImpl postService;
    private final AuthService authService;
    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<?> posting(@Valid @RequestBody PostSaveRequestDto postSaveRequestDto, BindingResult bindingResult, HttpServletRequest request){
        String currentUsername = authService.getCurrentSessionUsername(request);
        Long userId = memberRepository.findIdByUsername(currentUsername);
        //로그인 검사
        if(currentUsername == null){
            bindingResult.addError(new FieldError("PostSaveRequestDto","username","로그인이 필요합니다."));
        }
        if(!postService.checkLoginId(currentUsername)){
            bindingResult.addError(new FieldError("PostSaveRequestDto","user_Id","존재하지 않는 아이디 입니다."));
        }
        postService.createPost(postSaveRequestDto,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시물 작성을 성공하였습니다");
    }

    @PatchMapping("/{post_id}")
    public ResponseEntity<?> updating(@Valid @RequestBody PostSaveRequestDto requestDto, BindingResult bindingResult, HttpServletRequest request, @PathVariable Long post_id){
        String currentUsername = authService.getCurrentSessionUsername(request);
        //로그인 검사
        if(currentUsername == null){
            bindingResult.addError(new FieldError("PostSaveRequestDto","username","로그인이 필요합니다."));
        }
        //다른유저가 수정할 경우 금지
        if(postService.checkWriterEquals(currentUsername,post_id)){
            bindingResult.addError(new FieldError("PostSaveRequestDto","username","수정 권한이 없습니다."));
        }

        postService.updatePost(post_id,requestDto);
        return ResponseEntity.status(HttpStatus.OK).body("게시물 수정을 성공하였습니다");
    }
    @DeleteMapping("/{post_id}")
    public ResponseEntity<?> removing(@Valid @PathVariable Long post_id,BindingResult bindingResult,HttpServletRequest request){
        String currentUsername = authService.getCurrentSessionUsername(request);
        //로그인 검사
        if(currentUsername == null){
            bindingResult.addError(new FieldError("PostSaveRequestDto","username","로그인이 필요합니다."));
        }
        //다른유저가 수정할 경우 금지
        if(postService.checkWriterEquals(currentUsername,post_id)){
            bindingResult.addError(new FieldError("PostSaveRequestDto","username","수정 권한이 없습니다."));
        }

        postService.deletePost(post_id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("게시물 삭제를 성공하였습니다");
    }
    @GetMapping("/view/{post_id}")
    public ResponseEntity<DetailPostResponseDto> getPost(@Valid @PathVariable Long post_id, BindingResult bindingResult, HttpServletRequest request){
        DetailPostResponseDto responseDto = postService.getPostInfo(post_id);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
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
    public Page<PostListResponseDto> getPostList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "create_date,desc") String sort
    ){
        return postService.getPostList(keyword, category, page, size, sort);
    }
}
