package com.baki.backer.domain.post;


import com.baki.backer.domain.post.dto.DetailPostResponseDto;
import com.baki.backer.domain.post.dto.PostSaveRequestDto;

public interface PostService {

    /**
     * 게시글 등록
     */
    void createPost(PostSaveRequestDto request, String currentUsername);

    /**
     * 게시글 수정
     *
     *
     */
    void updatePost(Integer postId, PostSaveRequestDto request);

    /**
     * 게시글 삭제
     */
    void deletePost(Integer postId);

    /**
     * 게시글 1개 조회
     */
    DetailPostResponseDto getPostInfo(Integer post_id);

    /**
     * 검색 조건에 따른 게시글 리스트 조회 + 페이징
     *
     *
     */
    void getPostList();

}
