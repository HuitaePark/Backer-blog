package com.baki.backer.domain.post;

import com.baki.backer.domain.post.dto.PostRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostServiceImpl postService;

    @PostMapping
    public ResponseEntity<?> posting(@Valid @RequestBody PostRequestDto postRequestDto, BindingResult bindingResult, HttpServletRequest request){
        HttpSession session = request.getSession(false);
        String currentUsername = (String) session.getAttribute("username");

        if(!postService.checkLoginId(currentUsername)){
            bindingResult.addError(new FieldError("PostRequestDto","user_Id","존재하지 않는 아이디 입니다."));
        }
        postService.createPost(postRequestDto,currentUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시물 작성을 성공하였습니다");
    }
}
