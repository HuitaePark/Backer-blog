package com.baki.backer.domain.post;

import com.baki.backer.domain.post.dto.PostSaveDto;
import com.baki.backer.domain.post.dto.PostUpdateDto;

import javax.annotation.processing.FilerException;

public interface PostService {

    /**
     * 게시글 등록
     */
    void postSave(PostSaveDto postSaveDto) throws FilerException;
    /**
     * 게시글 수정
     */
    void postUpdate(Integer id, PostUpdateDto postUpdateDto);
    /**
     * 게시글 삭제
     */
    void postDelete(Integer id);

    /**
     * 게시글 1개 조회
     */
    void getPostInfo(Integer id);

    /**
     * 검색 조건에 따른 게시글 리스트 조회 + 페이징
     */

}


