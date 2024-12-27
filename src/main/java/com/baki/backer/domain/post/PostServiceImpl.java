package com.baki.backer.domain.post;

import com.baki.backer.domain.member.repository.MemberRepository;
import com.baki.backer.domain.post.dto.PostRequestDto;
import com.baki.backer.domain.post.dto.PostUpdateDto;
import com.baki.backer.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    //외래키 제약조건 검증
    public boolean checkLoginId(String currentUsername) {
        return memberRepository.existsById(memberRepository.findIdByUsername(currentUsername));
    }

    /** 게시물 작성
     *
     * @param request 입력정보
     * @param currentUsername 현재 세션의 유저 아이디
     */
    @Override
    public void createPost(PostRequestDto request,String currentUsername) {
        Post post = request.toEntity();
        post.setWriter_username(currentUsername);
        postRepository.save(post);
    }

    @Override
    public void updatePost(Integer post_id, PostUpdateDto update) {

    }

    @Override
    public void postDelete(Integer post_id) {

    }

    @Override
    public void getPostInfo(Integer post_id) {

    }

}
