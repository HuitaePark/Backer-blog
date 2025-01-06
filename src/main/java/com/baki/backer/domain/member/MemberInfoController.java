package com.baki.backer.domain.member;

import com.baki.backer.domain.member.dto.MemberInfoDto;
import com.baki.backer.domain.member.dto.MemberUpdateDto;
import com.baki.backer.domain.member.dto.MyPageResponseDto;
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
     * GET /api/members/{id}
     *
     * 세션의 username -> DB 조회 -> ID 비교 -> 일치 시 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberInfoDto> getMember(@PathVariable Long id, HttpServletRequest request) {
        // 세션 가져오기 (있으면 가져오고, 없으면 null)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            // 세션이 없거나 username이 없으면 인증 안 된 상태
            return ResponseEntity.status(401).build(); // UNAUTHORIZED
        }

        // 세션에 저장된 username
        String usernameInSession = (String) session.getAttribute("username");

        // username으로 멤버 조회 후 ID 비교 로직
        // [방법1] 서비스 단에서 ID 비교
        // MemberInfoDto memberInfo = memberInfoService.getMemberInfoByUsernameAndCheckId(id, usernameInSession);
        // return ResponseEntity.ok(memberInfo);

        // [방법2] 직접 controller에서 비교 (아래는 단순 예시)
        //  - 우선 여기서는 기존 getMemberInfo()를 그대로 사용하되, 컨트롤러에서 체크하는 형태로 해봅니다.
        MemberInfoDto memberInfo = memberInfoService.getMemberInfo(id);
        // 세션 username -> 실제 Member 가져오기
        //  (엔티티 직접 가져오려면, memberRepository.findByUsername(usernameInSession) 필요)
        //  여기서는 DTO에서 username 비교 예시
        if (!memberInfo.getUsername().equals(usernameInSession)) {
            return ResponseEntity.status(403).build(); // FORBIDDEN
        }

        return ResponseEntity.ok(memberInfo);
    }

    /**
     * 2) 멤버 비밀번호 & 이름 수정
     * PUT /api/members/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<MemberInfoDto> updateMember(@PathVariable Long id,
                                                      @RequestBody MemberUpdateDto updateDto,
                                                      HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            // 세션이 없거나 username이 없으면 인증 안 된 상태
            return ResponseEntity.status(401).build(); // UNAUTHORIZED
        }

        String usernameInSession = (String) session.getAttribute("username");

        // 서비스 레이어에서 ID & usernameInSession을 비교 후 업데이트
        MemberInfoDto updatedMemberInfo = memberInfoService.updateMember(id, usernameInSession, updateDto);
        return ResponseEntity.ok(updatedMemberInfo);
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<MyPageResponseDto> getPostAndComment(
            @PathVariable("id") Long memberId,
            HttpServletRequest request
    ) {
        try {
            // 1) 세션 가져오기
            HttpSession session = request.getSession(false);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null); // 또는 에러 메시지 포함
            }

            // 2) 세션에서 username 가져오기
            String username = (String) session.getAttribute("username");
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null); // 또는 에러 메시지 포함
            }

            // 3) DB에서 세션 사용자 정보를 가져오기
            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다: " + username));

            // 4) ID 일치 여부 확인
            if (!member.getId().equals(memberId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null); // 또는 에러 메시지 포함
            }

            // 5) 서비스 호출
            MyPageResponseDto response = memberInfoService.getPostAndComments(username);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 예외 발생 시 에러 응답 전송
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // 또는 에러 메시지 포함
        } catch (Exception e) {
            // 일반 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 또는 에러 메시지 포함
        }
    }
}