package com.baki.backer.domain.post;

import com.baki.backer.domain.member.MemberService;
import com.baki.backer.domain.post.dto.PostSaveRequestDto;
import com.baki.backer.domain.post.dto.PostUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostServiceImpl postService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<?> posting(@Valid @RequestBody PostSaveRequestDto postSaveRequestDto, BindingResult bindingResult, HttpServletRequest request){
        String currentUsername = memberService.getCurrentSessionUsername(request);

        if(!postService.checkLoginId(currentUsername)){
            bindingResult.addError(new FieldError("PostSaveRequestDto","user_Id","존재하지 않는 아이디 입니다."));
        }
        postService.createPost(postSaveRequestDto,currentUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시물 작성을 성공하였습니다");
    }

    @PatchMapping("/post/{post_id}")
    public ResponseEntity<?> updating(@Valid @RequestBody PostSaveRequestDto requestDto, BindingResult bindingResult, HttpServletRequest request, @PathVariable Integer post_id){
        String currentUsername = memberService.getCurrentSessionUsername(request);
        //로그인 검사
        if(currentUsername == null){
            bindingResult.addError(new FieldError("PostUpdateRequestDto","username","로그인이 필요합니다."));
        }
        //다른유저가 수정할 경우 금지
        if(postService.checkWriterEquals(currentUsername,post_id)){
            bindingResult.addError(new FieldError("PostUpdateRequestDto","username","수정 권한이 없습니다."));
        }

        postService.updatePost(post_id,requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시물 수정을 성공하였습니다");
    }
}
