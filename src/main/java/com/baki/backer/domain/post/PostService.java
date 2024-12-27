package com.baki.backer.domain.post;


import com.baki.backer.domain.post.dto.PostRequestDto;
import com.baki.backer.domain.post.dto.PostUpdateDto;

public interface PostService {

    /**
     * 게시글 등록
     */
    void createPost(PostRequestDto request,String currentUsername);

    /**
     * 게시글 수정
     */
    void updatePost(Integer post_id, PostUpdateDto update);

    /**
     * 게시글 삭제
     */
    void postDelete(Integer post_id);

    /**
     * 게시글 1개 조회
     */
    void getPostInfo(Integer post_id);

    /**
     * 검색 조건에 따른 게시글 리스트 조회 + 페이징
     */

}
