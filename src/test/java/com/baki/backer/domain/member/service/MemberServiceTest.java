package com.baki.backer.domain.member.service;

import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRole;
import com.baki.backer.domain.member.MemberService;
import com.baki.backer.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jdk.jfr.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.baki.backer.domain.member.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest {

    private MemberService memberService;
    private MemberRepository memberRepository;

    JoinRequestDto joinRequestDto;
    LoginRequestDto loginRequestDto;

    @Autowired
    public MemberServiceTest(MemberRepository memberRepository, MemberService memberService) {
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    @BeforeEach
    void setUp() {
        joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername("test");
        joinRequestDto.setPassword("password123");
        joinRequestDto.setPasswordCheck("password123");
        joinRequestDto.setName("Test");

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("testuser");
        loginRequestDto.setPassword("password123");

        Member member = Member.builder()
                .id(1)
                .username("testuser")
                .password("encodedPassword123")
                .name("Test User")
                .user_role(MemberRole.USER)
                .build();

        memberRepository.save(member);
    }

    @Test
    @Name("로그인 아이디 중복 확인")
    void checkLoginIdDuplicate() {
        assertTrue(memberService.checkLoginIdDuplicate("testuser"));
        assertFalse(memberService.checkLoginIdDuplicate("notFounduser"));
    }

    @Test
    @Name("닉네임 중복 확인")
    void checkNameDuplicate() {
        assertTrue(memberService.checkNameDuplicate("Test User"));
        assertFalse(memberService.checkNameDuplicate("notFounduser"));
    }

    @Test
    void join() {
        memberService.join(joinRequestDto);
        assertTrue(memberService.checkLoginIdDuplicate("test"));
        assertTrue(memberService.checkNameDuplicate("Test"));
    }

    @Test
    void getLoginMemberByUserId() {
        memberService.login(loginRequestDto);
        Member loginMember = memberService.getLoginMemberByUserId(1);
        assertNotNull(loginMember);
    }

    @Test
    void getLoginMemberByUsername() {
        memberService.login(loginRequestDto);
        Member loginMember = memberService.getLoginMemberByUsername("testuser");
        assertNotNull(loginMember);
    }

    @Test
    void getCurrentSessionUsername() {
        // given
        memberService.login(loginRequestDto);
        Member loginMember = memberService.getLoginMemberByUsername("testuser");

        // MockHttpServletRequest 생성
        MockHttpServletRequest request = new MockHttpServletRequest();

        // getSession(true)는 기존 세션이 없으면 새로 만들어준다
        MockHttpSession session = (MockHttpSession) request.getSession(true);

        // when
        session.setAttribute("username", loginMember.getUsername());

        // then
        assertEquals(loginMember.getUsername(), session.getAttribute("username"));
    }
}