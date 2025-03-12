package com.baki.backer.domain.member;

import com.baki.backer.domain.member.dto.*;
import com.baki.backer.global.common.ApiResponseDto;
import com.baki.backer.global.error.ErrorResponse;
import com.baki.backer.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberInfoController {

    private final MemberInfoService memberInfoService;

    /**
     * 1) 멤버 조회
     * GET /member/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<MemberInfoDto>> getMember(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String usernameInSession = (String) session.getAttribute("username");

        //조회하려는 멤버와 현재 세션의 멤버가 다를시 에러 반환
        MemberInfoDto memberInfo = memberInfoService.getMemberInfo(id);
        if (!memberInfo.getUsername().equals(usernameInSession)) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.error(errorResponse));
        }
        return ResponseEntity.ok(ResponseUtil.ok(memberInfo));
    }

    /**
     * 2) 멤버 비밀번호 & 이름 수정
     * PATCH /member/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDto<MemberInfoDto>> updateMember(@PathVariable Long id,
                                                                      @RequestBody MemberUpdateDto updateDto,
                                                                      HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String usernameInSession = (String) session.getAttribute("username");

        // 서비스 레이어에서 ID와 username 비교 후 업데이트
        MemberInfoDto updatedMemberInfo = memberInfoService.updateMember(id, usernameInSession, updateDto);
        return ResponseEntity.ok(ResponseUtil.ok(updatedMemberInfo));
    }

    /**
     * 3) 회원의 게시글 조회
     *
     */
    @GetMapping("/{id}/activity/post")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getAllPosts(@PathVariable("id") Long memberId) {
            //멤버 아이디로 포스트 검색후 반환
            List<PostResponseDto> response = memberInfoService.getAllPosts(memberId);
            return ResponseEntity.ok(ResponseUtil.ok(response));
    }

    /**
     * 4) 회원의 댓글 조회
     *
     */
    @GetMapping("/{id}/activity/comment")
    public ResponseEntity<ApiResponseDto<List<CommentResponseDto>>> getAllComments(@PathVariable("id") Long memberId) {
            //멤버 아이디로 코멘트 검색후 반환
            List<CommentResponseDto> response = memberInfoService.getAllComments(memberId);
            return ResponseEntity.ok(ResponseUtil.ok(response));
        }
}
