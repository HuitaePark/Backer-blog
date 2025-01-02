package com.baki.backer.domain.post.repository;

import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRole;
import com.baki.backer.domain.member.repository.MemberRepository;
import com.baki.backer.domain.post.Category;
import com.baki.backer.domain.post.Post;
import com.baki.backer.domain.post.dto.PostListResponseDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    private Member member;
    private Post post;

    @BeforeEach
    void setUp() {
        // 1) Member, Post 엔티티 생성
        member = Member.builder()
                .name("Existing User")
                .password("password123")
                .user_role(MemberRole.USER)
                .username("existingUser")
                .build();

        // 카테고리가 QUESTION이라고 가정
        post = Post.builder()
                .title("Test Post")
                .content("Hello World")
                .category_id(Category.QUESTION)
                .member(member)
                .build();

        // 2) 실제 DB에 저장
        Member savedMember = memberRepository.save(member);
        Post savedPost = postRepository.save(post);

        // 3) auto-generated ID가 주입된 엔티티 반환
        member = savedMember;
        post = savedPost;

        // DB와 영속성 컨텍스트 동기화
        em.flush();
        em.clear();
    }

    @Test
    void findPostWithComment() {
        Post findpost = postRepository.findPostWithComment(1);
        assertNotNull(findpost, "findPost가 null이 아니어야 함");
        assertEquals(findpost.getTitle(), post.getTitle(),"제목이 일치해야함");
    }

    @Test
    void searchPost(){
        // given
        // 검색 키워드: "Hello" / 카테고리: "3" (QUESTION)
        String keyword = "Hello";
        String categoryParam = "1"; // QUESTION을 가정
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "create_date"));

        // when
        // 실제 DB 쿼리를 통해 검색 결과 받아오기
        Page<PostListResponseDto> resultPage
                = postRepository.searchPost(keyword, categoryParam, pageable);

        // then
        assertNotNull(resultPage, "검색 결과 Page가 null이면 안 됩니다.");
        assertFalse(resultPage.isEmpty(), "검색 결과가 비어 있으면 안 됩니다.");

        // 첫 번째 결과물 검증
        PostListResponseDto firstPost = resultPage.getContent().get(0);
        assertEquals("Test Post", firstPost.getTitle(), "제목이 일치해야 합니다.");
        assertEquals("Hello World", firstPost.getContent(), "내용이 일치해야 합니다.");
        assertEquals(member.getName(), firstPost.getName(), "작성자 이름이 일치해야 합니다.");

        // 전체 개수 검증
        assertEquals(1, resultPage.getTotalElements(), "전체 결과 개수는 1개여야 합니다.");
    }
}