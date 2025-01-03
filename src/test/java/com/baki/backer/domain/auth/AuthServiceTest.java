package com.baki.backer.domain.auth;

import com.baki.backer.domain.auth.dto.JoinRequestDto;
import com.baki.backer.domain.auth.dto.LoginRequestDto;
import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRole;
import com.baki.backer.domain.member.MemberRepository;
import jdk.jfr.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    private AuthService authService;
    private MemberRepository memberRepository;

    JoinRequestDto joinRequestDto;
    LoginRequestDto loginRequestDto;

    @Autowired
    public AuthServiceTest(MemberRepository memberRepository, AuthService authService) {
        this.memberRepository = memberRepository;
        this.authService = authService;
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
                .id(1L)
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
        assertTrue(authService.checkLoginIdDuplicate("testuser"));
        assertFalse(authService.checkLoginIdDuplicate("notFounduser"));
    }

    @Test
    @Name("닉네임 중복 확인")
    void checkNameDuplicate() {
        assertTrue(authService.checkNameDuplicate("Test User"));
        assertFalse(authService.checkNameDuplicate("notFounduser"));
    }

    @Test
    void join() {
        authService.join(joinRequestDto);
        assertTrue(authService.checkLoginIdDuplicate("test"));
        assertTrue(authService.checkNameDuplicate("Test"));
    }

    @Test
    void getLoginMemberByUserId() {
        authService.login(loginRequestDto);
        Member loginMember = authService.getLoginMemberByUserId(1L);
        assertNotNull(loginMember);
    }

    @Test
    void getLoginMemberByUsername() {
        authService.login(loginRequestDto);
        Member loginMember = authService.getLoginMemberByUsername("testuser");
        assertNotNull(loginMember);
    }

    @Test
    void getCurrentSessionUsername() {
        // given
        authService.login(loginRequestDto);
        Member loginMember = authService.getLoginMemberByUsername("testuser");

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