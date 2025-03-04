package com.baki.backer.domain.member;

import com.baki.backer.domain.member.MemberInfoService;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.member.dto.MemberInfoDto;
import com.baki.backer.domain.member.dto.MemberUpdateDto;
import com.baki.backer.domain.member.dto.MyPageResponseDto;
import com.baki.backer.global.common.ApiResponseDto;
import com.baki.backer.global.error.ErrorResponse;
import com.baki.backer.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberInfoController {

    private final MemberInfoService memberInfoService;
    private final MemberRepository memberRepository;

    /**
     * 1) 멤버 조회
     * GET /member/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<MemberInfoDto>> getMember(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.error(errorResponse));
        }
        String usernameInSession = (String) session.getAttribute("username");

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
        if (session == null || session.getAttribute("username") == null) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.error(errorResponse));
        }
        String usernameInSession = (String) session.getAttribute("username");

        // 서비스 레이어에서 ID와 username 비교 후 업데이트
        MemberInfoDto updatedMemberInfo = memberInfoService.updateMember(id, usernameInSession, updateDto);
        return ResponseEntity.ok(ResponseUtil.ok(updatedMemberInfo));
    }

    /**
     * 3) 회원의 게시글/댓글 활동 조회
     * GET /member/{id}/activity
     */
    @GetMapping("/{id}/activity")
    public ResponseEntity<ApiResponseDto<MyPageResponseDto>> getPostAndComment(@PathVariable("id") Long memberId,
                                                                               HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.error(errorResponse));
            }

            String username = (String) session.getAttribute("username");
            if (username == null) {
                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.error(errorResponse));
            }

            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다: " + username));

            if (!member.getId().equals(memberId)) {
                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.error(errorResponse));
            }

            MyPageResponseDto response = memberInfoService.getPostAndComments(username);
            return ResponseEntity.ok(ResponseUtil.ok(response));

        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.error(errorResponse));
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtil.error(errorResponse));
        }
    }
}
