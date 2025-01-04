package com.baki.backer.domain.member;

import com.baki.backer.domain.comment.CommentRepository;
import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.member.dto.*;
import com.baki.backer.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberInfoService {

    private final MemberRepository memberRepository;
    private PostRepository postRepository;
    private CommentRepository commentRepository;


    /**
     * 단일 멤버 조회
     */
    public MemberInfoDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다. id=" + memberId));
        return new MemberInfoDto(member);
    }

    /**
     * 로그인된 사용자(세션에 있는 username)의 멤버 ID와
     * 수정 요청으로 들어온 {id}가 같은지 확인 후 비밀번호/이름을 수정
     *
     * @param id (PathVariable)
     * @param usernameInSession (세션에서 가져온 username)
     * @param updateDto (비밀번호, 이름 수정 정보)
     */
    public MemberInfoDto updateMember(Long id, String usernameInSession, MemberUpdateDto updateDto) {
        // 세션에 있는 username으로 DB 조회
        Member memberInSession = memberRepository.findByUsername(usernameInSession)
                .orElseThrow(() -> new IllegalArgumentException("세션 정보와 일치하는 멤버가 없습니다."));

        // 실제 DB상의 ID가 PathVariable과 일치하는지 확인
        if (!memberInSession.getId().equals(id)) {
            // 본인의 ID가 아닌 경우 예외 처리 (혹은 커스텀 예외나 반환 처리)
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 비밀번호와 이름을 수정
        memberInSession.setPassword(updateDto.getPassword());
        memberInSession.setName(updateDto.getName());
        // 변경 사항 저장
        Member saved = memberRepository.save(memberInSession);

        return new MemberInfoDto(saved);
    }

    public MyPageResponseDto getPostAndComments(String username){
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다: " + username));

        // member를 통해 게시글 목록, 댓글 목록 조회
        var postList = postRepository.findAllByMember(member);
        var commentList = commentRepository.findAllByMember(member);

        // DTO 변환
        List<PostResponseDto> postDtos = postList.stream()
                .map(PostResponseDto::of)
                .toList();

        List<CommentResponseDto> commentDtos = commentList.stream()
                .map(CommentResponseDto::of)
                .toList();

        return MyPageResponseDto.builder()
                .posts(postDtos)
                .comments(commentDtos)
                .build();
    }
}