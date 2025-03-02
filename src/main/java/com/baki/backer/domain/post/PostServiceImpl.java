package com.baki.backer.domain.post;

import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.post.dto.DetailPostResponseDto;
import com.baki.backer.domain.post.dto.PostListResponseDto;
import com.baki.backer.domain.post.dto.PostSaveRequestDto;
import com.baki.backer.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    //외래키 제약조건 검증
    public boolean checkLoginId(String currentUsername) {
        return memberRepository.existsById(memberRepository.findIdByUsername(currentUsername)); //이거 좀 맘에 안드는데 리펙토링 대상
    }

    /**
     * 게시물 작성
     *
     * @param requestDto 입력정보
     * @param userId     현재 세션의 유저 아이디
     */
    @Override
    public void createPost(PostSaveRequestDto requestDto, Long userId) {
        // 현재 유저의 Member 엔티티 조회
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Post post = Post.builder()
                .member(member)
                .category_id(requestDto.category_id())
                .title(requestDto.title())
                .content(requestDto.content())
                .build();
        // 게시글 저장
        postRepository.save(post);
    }

    /**
     *
     * @param postId 수정할 게시물 아이디
     * @param request 수정된 제목이랑 내용,카테고리를 받아서 넘김
     */
    @Override
    public void updatePost(Long postId, PostSaveRequestDto request) {
        Post existingPost = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        existingPost.updateDto(request);
        postRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long post_id) {
        Post existingPost = postRepository.findById(post_id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        postRepository.delete(existingPost);
    }

    @Override
    public DetailPostResponseDto getPostInfo(Long post_id) {
        Post post = postRepository.findPostWithComment(post_id);
        if (post == null) {throw new RuntimeException("게시글을 찾을 수 없습니다.");}
        return new DetailPostResponseDto(post);
    }
    /**
     * 게시물을 검색하고 페이징 처리된 결과를 반환합니다.
     *
     * @param keyword  검색 키워드 (옵션)
     * @param category 게시물 카테고리 ID (옵션)
     * @param page     페이지 번호 (기본값: 1)
     * @param size     페이지 크기 (기본값: 20)
     * @param sort     정렬 기준 (예: "create_date,desc")
     * @return 페이징 처리된 게시물 목록
     */
    @Override
    public Page<PostListResponseDto> getPostList(String keyword, String category, Integer page, Integer size, String sort) {
        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = (size == null || size < 1) ? 20 : size;

        System.out.println("입력받은 sort 값: " + sort); // 디버깅 로그

        // 기본 정렬: create_date 내림차순
        Sort sortOrder = Sort.by("create_Date").descending(); // 대문자 D로 맞춤

        if(sort != null && !sort.isEmpty()){ // 조건문 수정 (! 추가)
            System.out.println("sort 파싱 시작");
            String[] sortParams = sort.split(",");
            System.out.println("분할된 파라미터: " + Arrays.toString(sortParams));

            if (sortParams.length == 2){
                String sortField = sortParams[0];
                Sort.Direction direction = sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                sortOrder = Sort.by(direction, sortField);
                System.out.println("적용된 정렬: " + direction + " " + sortField);
            }
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortOrder);
        System.out.println("최종 정렬 정보: " + pageable.getSort());

        return postRepository.searchPost(keyword, category, pageable);
    }

    public boolean checkWriterEquals(String currentUsername,Long post_id){
        Post existingPost = postRepository.findById(post_id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        String writerUsername = existingPost.getMember().getName();
        return writerUsername.equals(currentUsername);
    }
}
