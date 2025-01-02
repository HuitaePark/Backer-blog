package com.baki.backer.domain.auth;


import com.baki.backer.domain.auth.dto.JoinRequestDto;
import com.baki.backer.domain.auth.dto.LoginRequestDto;
import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;

    /**
     * loginId 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkLoginIdDuplicate(String username){
        return memberRepository.existsByUsername(username);
    }
    /**
     * nickname 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkNameDuplicate(String name) {
        return memberRepository.existsByName(name);
    }
    /**
     * 회원가입 기능
     * 화면에서 JoinRequestDto(loginId, password, nickname)을 입력받아 User 변환 후 저장
     * loginId, nickname 중복 체크는 Controller 진행 => 에러 메세지 출력을 위해
     */
    public void join(JoinRequestDto request){
        memberRepository.save(request.toEntity());
    }
    /**
     *  로그인 기능
     *  화면에서 LoginRequestDto(loginId, password)을 입력받아 loginId와 password 일치하면 User return
     *  loginId가 존재하지 않거나 password 일치하지 않으면 null return
     */
    public Member login(LoginRequestDto request){
        Optional<Member> optionalMember = memberRepository.findByUsername(request.getUsername());

        //일치하는 멤버가 없으면 널
        if (optionalMember.isEmpty()){
            return null;
        }
        Member member = optionalMember.get();

        //찾아온 멤버의 패스워드와 입력된 패스워드가 다르면 널
        if (!member.getPassword().equals(request.getPassword())){
            return null;
        }
        return member;
    }
    /**
     * username 입력받아 Member를 return 해주는 기능
     * 인증, 인가 시 사용
     * username이 null이거나(로그인 X) username으로 찾아온 Member가 없으면 null return
     * username으로 찾아온 member가 존재하면 Member return
     */
    public Member getLoginMemberByUserId(Integer userId) {
        if(userId == null) return null;

        Optional<Member> optionalUser = memberRepository.findById(userId);
        return optionalUser.orElse(null);

    }
    public Member getLoginMemberByUsername(String username) {
        if(username == null) return null;

        Optional<Member> optionalUser = memberRepository.findByUsername(username);
        return optionalUser.orElse(null);

    }

    /**
     *
     * @param request 요청의 리퀘스트를 받음
     * @return 현재 세션의 유저네임을 반환
     */
    public String getCurrentSessionUsername(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        return (String) session.getAttribute("username");
    }
}
