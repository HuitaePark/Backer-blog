package com.baki.backer.repository;

import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.member.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    //give
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

    @Test
    void existsByUsername() {
        assertTrue(memberRepository.existsByName("박희"),"아이디 중복 체크");
        assertFalse(memberRepository.existsByName("박zl"),"아이디 비중복 체크");

    }

    @Test
    void existsByName() {

    }

    @Test
    void findByUsername() {
    }
}