package com.baki.backer.service;

import com.baki.backer.domain.member.DTO.LoginRequest;
import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.member.MemberRole;
import com.baki.backer.domain.member.MemberService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {
    //give
    // when
    // then
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        // 트랜잭션 롤백과 중복 방지를 위해 명시적 클리어
        memberRepository.deleteAll();

        //회원 데이터 생성
        LocalDateTime currentTimestamp = LocalDateTime.now();
        Member member = Member.builder()
                .username("parkhee") // 유니크한 username 설정
                .password("1234")
                .name("박희")
                .crate_date(currentTimestamp)
                .user_role(MemberRole.USER)
                .build();
        memberRepository.save(member);
    }

    @Nested
    @DisplayName("중복 테스트")
    class DuplicateTest {
        @Test
        @DisplayName("아이디 중복되는지 확인")
        void checkUsernameDuplicate(){
            // Given: 아이디가 "hee"인 회원 저장

            // When & Then: "박희"는 중복, "바키"는 중복이 아님
            assertTrue(memberService.checkLoginIdDuplicate("parkhee"), "아이디 'parkhee'는 중복되어야 합니다.");
            assertFalse(memberService.checkLoginIdDuplicate("qwer"), "아이디 'qwer'는 중복되지 않아야 합니다.");
        }

        @Test
        @DisplayName("이름이 중복되는지 확인")
        void checkNameDuplicate() {
            // Given: 이름이 "박희"인 회원 저장


            // When & Then: "박희"는 중복, "바키"는 중복이 아님
            assertTrue(memberService.checkNameDuplicate("박희"), "이름 '박희'는 중복되어야 합니다.");
            assertFalse(memberService.checkNameDuplicate("바키"), "이름 '바키'는 중복되지 않아야 합니다.");
        }
    }

    @Test
    @DisplayName("회원가입 테스트")
    void join() {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        Member member = Member.builder()
                .username("hee") // 유니크한 username 설정

                .password("1234")
                .name("박희")
                .crate_date(currentTimestamp)
                .user_role(MemberRole.USER)
                .build();
        memberRepository.save(member);

        assertEquals("박희",member.getName());
    }

    @Test
    void login() {

        LoginRequest loginRequest1 = new LoginRequest();
        LoginRequest loginRequest2 = new LoginRequest();

        loginRequest1.setUsername("parkhee");
        loginRequest1.setPassword("1234");
        loginRequest2.setUsername("Baki");
        loginRequest2.setPassword("wrongpassword");

        Member member = memberService.login(loginRequest1);
        assertNotNull(member, "로그인된 회원은 null이 아니어야 합니다.");
        assertEquals("박희",member.getName());

        Member failedLogin = memberService.login(loginRequest2);

        // Then: 로그인에 실패하여 null이 반환되는지 확인
        assertNull(failedLogin, "잘못된 비밀번호로 로그인 시도 시 null이 반환되어야 합니다.");
    }

    @Test
    void getLoginMemberByUserId() {

        // When: "parkhee"로 로그인
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("parkhee");
        loginRequest.setPassword("1234");
        Member loggedInMember = memberService.login(loginRequest);

        // Then: 로그인된 회원이 null이 아니고, 이름이 일치하는지 확인
        assertNotNull(loggedInMember, "로그인된 회원은 null이 아니어야 합니다.");
        assertEquals("박희", loggedInMember.getName(), "회원 이름이 일치해야 합니다.");

        // When: 저장된 회원의 ID로 조회
        Integer userId = loggedInMember.getId();
        Member retrievedMember = memberService.getLoginMemberByUserId(userId);

        // Then: 조회된 회원이 로그인된 회원과 동일한지 확인
        assertNotNull(retrievedMember, "조회된 회원은 null이 아니어야 합니다.");
        assertEquals(loggedInMember, retrievedMember, "조회된 회원은 로그인된 회원과 동일해야 합니다.");

    }

    @Test
    void getLoginMemberByUsername() {
        // When: "parkhee"로 로그인
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("parkhee");
        loginRequest.setPassword("1234");
        Member loggedInMember = memberService.login(loginRequest);

        // Then: 로그인된 회원이 null이 아니고, 이름이 일치하는지 확인
        assertNotNull(loggedInMember, "로그인된 회원은 null이 아니어야 합니다.");
        assertEquals("박희", loggedInMember.getName(), "회원 이름이 일치해야 합니다.");

        // When: 저장된 회원의 유저네임으로 조회
        String username = loggedInMember.getUsername();
        Member retrievedMember = memberService.getLoginMemberByUsername(username);

        // Then: 조회된 회원이 로그인된 회원과 동일한지 확인
        assertNotNull(retrievedMember, "조회된 회원은 null이 아니어야 합니다.");
        assertEquals(loggedInMember, retrievedMember, "조회된 회원은 로그인된 회원과 동일해야 합니다.");
    }
}