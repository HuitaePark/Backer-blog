package com.baki.backer.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * 테스트 데이터 생성 메서드
     */
    private Member createMember() {
        return Member.builder()
                .name("Existing User")
                .password("password123")
                .user_role(MemberRole.USER)
                .username("existingUser")
                .build();
    }

    @Test
    @DisplayName("유저의 아이디가 존재하는지 검사")
    void existsByUsername() {
        Member member = createMember();
        memberRepository.save(member);
        assertTrue(memberRepository.existsByUsername("existingUser"),"존재하는 유저 확인");
        assertFalse(memberRepository.existsByUsername("undefinedUser"),"존재하지 않는 유저 확인");
    }

    @Test
    void existsByName() {
        Member member = createMember();
        memberRepository.save(member);
        assertTrue(memberRepository.existsByName("Existing User"),"존재하는 유저 확인");
        assertFalse(memberRepository.existsByName("undefinedUser"),"존재하지 않는 유저 확인");
    }

    @Test
    void existsById() {
        Member member = createMember();
        memberRepository.save(member);

        assertTrue(memberRepository.existsById(1L),"존재하는 유저 확인");
        assertFalse(memberRepository.existsById(2L),"존재하지 않는 유저 확인");
    }

    @Test
    void findByUsername() {
        Member member = createMember();
        memberRepository.save(member);
        Member findMember = memberRepository.findByUsername("existingUser")
                .orElseThrow(() -> new RuntimeException("User not found"));
        assertEquals(member.getUsername(), findMember.getUsername());
    }

    @Test
    void findIdByUsername() {
        Member member = createMember();
        memberRepository.save(member);
        Long findMemberId = memberRepository.findIdByUsername("existingUser");

        assertEquals(member.getId(), findMemberId);
    }

}