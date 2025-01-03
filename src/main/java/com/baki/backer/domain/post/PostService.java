package com.baki.backer.domain.post;


import com.baki.backer.domain.post.dto.DetailPostResponseDto;
import com.baki.backer.domain.post.dto.PostListResponseDto;
import com.baki.backer.domain.post.dto.PostSaveRequestDto;
import org.springframework.data.domain.Page;

public interface PostService {

    /**
     * 게시글 등록
     */
    void createPost(PostSaveRequestDto request, Long userId);

    /**
     * 게시글 수정
     *
     *
     */
    void updatePost(Long postId, PostSaveRequestDto request);

    /**
     * 게시글 삭제
     */
    void deletePost(Long postId);

    /**
     * 게시글 1개 조회
     */
    DetailPostResponseDto getPostInfo(Long post_id);

    /**
     * 검색 조건에 따른 게시글 리스트 조회 + 페이징
     * keyword (string, optional): 검색 키워드.
     * category (string, optional): 게시물 카테고리 ID.
     * page (integer, optional): 페이지 번호 (기본값: 1).
     * size (integer, optional): 페이지 크기 (기본값: 20).
     * sort (string, optional): 정렬 기준 (예: create_date,asc).
     *
     */
    public Page<PostListResponseDto> getPostList(String keyword,String category,Integer page,Integer size,String sort);

}
