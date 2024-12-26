package com.baki.backer.domain.member;

import com.baki.backer.domain.member.DTO.JoinRequest;
import com.baki.backer.domain.member.DTO.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/session")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequest joinRequest, BindingResult bindingResult){
        if(memberService.checkLoginIdDuplicate(joinRequest.getUsername())){
            bindingResult.addError(new FieldError("joinRequest","username","로그인 아이디가 중복됩니다."));
        }
        if(memberService.checkNameDuplicate(joinRequest.getName())){
            bindingResult.addError(new FieldError("joinRequest","name","이름이 중복됩니다."));
        }
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())){
            bindingResult.addError(new FieldError("joinRequest","passwordCheck","비밀번호가 일치하지 않습니다."));
        }
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        memberService.join(joinRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest){
        Member member = memberService.login(loginRequest);

        if(member == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }

        //기존 세션 파기 후 새로운 세션 생성
        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);// session 없으면 생성
        session.setAttribute("username",member.getUsername());
        session.setMaxInactiveInterval(1800);

        return ResponseEntity.ok("로그인성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null){
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 성공");
    }
    @GetMapping("/admin")
    public ResponseEntity<?> adminPage(@SessionAttribute(name = "username", required = false) Integer Id){
        if(Id == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        Member loginMember = memberService.getLoginMemberByUserId(Id);
        if(loginMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        if(!loginMember.getUser_role().equals(MemberRole.ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        return ResponseEntity.ok("관리자 페이지 접근 허가");
    }
}
