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
        // 1) 세션 가져오기
        HttpSession session = request.getSession(false);
        if (session == null) {
            // 세션이 없으면 로그인 안 되어 있는 상태
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2) 세션에서 username 가져오기 (또는 userId가 있다면 userId)
        String username = (String) session.getAttribute("username");
        if (username == null) {
            // username이 없으면 역시 로그인 안 된 상태
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3) DB에서 세션 사용자 정보를 가져와서 PK(id)를 비교
        //    여기서는 예시로 username을 통해 Member 엔티티를 조회한다고 가정
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다: " + username));

        // 4) 현재 로그인한 멤버의 id와, 경로변수(memberId)가 일치하는지 확인
        if (!member.getId().equals(memberId)) {
            // 불일치하면 권한 없음 응답(403 등)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 5) 여기까지 통과하면 본인 정보가 맞으므로 서비스 호출
        MyPageResponseDto response = memberInfoService.getPostAndComments(username);
        return ResponseEntity.ok(response);
    }
}