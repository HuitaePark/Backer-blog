package com.baki.backer.domain.post.service;

import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRole;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.post.Category;
import com.baki.backer.domain.post.Post;
import com.baki.backer.domain.post.PostService;
import com.baki.backer.domain.post.dto.DetailPostResponseDto;
import com.baki.backer.domain.post.dto.PostListResponseDto;
import com.baki.backer.domain.post.dto.PostSaveRequestDto;
import com.baki.backer.domain.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
class PostServiceImplTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostService postService;

    private Member member;
    private Post post;

    private PostSaveRequestDto postSaveRequestDto;

    @BeforeEach
    void setUp() {
        // 1) 테스트용 Member 생성 & 저장
        member = Member.builder()
                .name("Existing User")
                .password("password123")
                .user_role(MemberRole.USER)
                .username("existingUser")
                .build();
        memberRepository.save(member);

        // 2) PostSaveRequestDto 생성
        postSaveRequestDto = new PostSaveRequestDto(
                "Test Post",
                "Hello World",
                Category.QUESTION
        );

        // 3) DTO -> 엔티티 변환
        Post postEntity = postSaveRequestDto.toEntity();
        // Post 엔티티에 Member를 연결
        postEntity.setMember(member);

        // 4) 실제 DB에 저장 (영속화)
        this.post = postRepository.save(postEntity);
    }
    @Test
    void checkLoginId() {

    }

    @Test
    void updatePost() {
        // given
        PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(
                "Test",
                "Hello",
                Category.QUESTION
        );

        // when
        postService.updatePost(1, postSaveRequestDto);

        // then
        // 1) 엔티티 조회
        Post updatedPost = postRepository.findById(1).orElse(null);

        // 2) null 체크
        assertNotNull(updatedPost,
                () -> "Post with ID=1 should exist, but it's null. Member=" + member);

        // 3) 수정 내용 검증 (예: 타이틀, 컨텐츠가 바뀌었는지)
        assertEquals("Test", updatedPost.getTitle());
        assertEquals("Hello", updatedPost.getContent());
        assertEquals(Category.QUESTION, updatedPost.getCategory_id());
    }

    @Test
    void deletePost() {
        postService.deletePost(1);
        assertNull(postRepository.findById(1).orElse(null));
    }

    @Test
    void getPostInfo() {
        // given
        Integer postId = post.getPost_id(); // 위에서 저장된 post의 PK

        // when
        DetailPostResponseDto result = postService.getPostInfo(postId);

        // then
        assertNotNull(result, "결과 DTO가 null이면 안 됩니다.");
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getContent(), result.getContent());
        // 필요한 경우, 댓글/작성자 정보 등 더 검증
    }

    @Test
    void getPostList() {
        // given
        String keyword = "Test Post";
        String categoryParam = "1";
        Integer page = 1;    // 1페이지
        Integer size = 5;    // 한 페이지에 5개
        String sort = "create_date,desc";

        // when
        Page<PostListResponseDto> resultPage
                = postService.getPostList(keyword, categoryParam, page, size, sort);

        // then
        assertNotNull(resultPage, "검색 결과 Page가 null이면 안 됩니다.");
        assertFalse(resultPage.isEmpty(), "검색 결과가 비어 있으면 안 됩니다.");

        // 첫 번째 엔티티 검증
        PostListResponseDto firstPost = resultPage.getContent().get(0);
        assertEquals("Test Post", firstPost.getTitle());
        assertEquals("Hello World", firstPost.getContent());
    }

}