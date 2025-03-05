package com.baki.backer.domain.auth;

import com.baki.backer.domain.auth.dto.JoinRequestDto;
import com.baki.backer.domain.auth.dto.LoginRequestDto;
import com.baki.backer.domain.auth.dto.LoginResponseDto;
import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRole;
import com.baki.backer.global.common.ApiResponseDto;
import com.baki.backer.global.common.SuccessMessageDto;
import com.baki.backer.global.error.ErrorResponse;
import com.baki.backer.global.util.ResponseUtil;
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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<ApiResponseDto<?>> join(@Valid @RequestBody JoinRequestDto joinRequestDto, BindingResult bindingResult){
        if(authService.checkLoginIdDuplicate(joinRequestDto.getUsername())){
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT, "중복된 아이디 입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseUtil.error(errorResponse));
        }
        if(authService.checkNameDuplicate(joinRequestDto.getName())){
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT, "중복된 이름 입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseUtil.error(errorResponse));
        }
        if(!joinRequestDto.getPassword().equals(joinRequestDto.getPasswordCheck())){
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtil.error(errorResponse));
        }
        if(bindingResult.hasErrors()){
            ErrorResponse errorResponse = ErrorResponse.of(bindingResult);
            return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
        }
        SuccessMessageDto successMessage = new SuccessMessageDto(201, "회원 가입이 완료되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.ok(successMessage));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest, BindingResult bindingResult){
        Member member = authService.login(loginRequestDto);

        if(member == null){
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND, "멤버를 찾을수 없습니다");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.error(errorResponse));
        }

        //기존 세션 파기 후 새로운 세션 생성
        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);// session 없으면 생성
        session.setAttribute("username",member.getUsername());
        session.setMaxInactiveInterval(1800);

        LoginResponseDto responseDto = new LoginResponseDto(member.getUsername(),member.getId(),"로그인에 성공하셨습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.ok(responseDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<?>> logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null){
            session.invalidate();
        }
        SuccessMessageDto successMessage = new SuccessMessageDto(200, "로그아웃이 성공되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.ok(successMessage));
    }
    @GetMapping("/admin")
    public ResponseEntity<ApiResponseDto<?>> adminPage(@SessionAttribute(name = "username", required = false) Long Id){
        if (Id == null) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.error(errorResponse));
        }

        Member loginMember = authService.getLoginMemberByUserId(Id);
        if (loginMember == null) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.error(errorResponse));
        }

        if (!loginMember.getUser_role().equals(MemberRole.ADMIN)) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN, "권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.error(errorResponse));
        }
        SuccessMessageDto successMessage = new SuccessMessageDto(200, "어드민 접근이 성공되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.ok(successMessage));
    }
}
