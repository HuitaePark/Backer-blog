package com.baki.backer.domain.post;

import com.baki.backer.domain.member.repository.MemberRepository;
import com.baki.backer.domain.post.dto.PostSaveRequestDto;
import com.baki.backer.domain.post.dto.PostUpdateRequestDto;
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
        return memberRepository.existsById(memberRepository.findIdByUsername(currentUsername)); //이거 좀 맘에 안드는데 리펙토링 대상
    }

    /** 게시물 작성
     *
     * @param request 입력정보
     * @param currentUsername 현재 세션의 유저 아이디
     */
    @Override
    public void createPost(PostSaveRequestDto request, String currentUsername) {
        Post post = request.toEntity();
        post.setWriter_username(currentUsername);
        post.setWriter_id(memberRepository.findIdByUsername(currentUsername));
        postRepository.save(post);
    }

    /**
     *
     * @param postId 수정할 게시물 아이디
     * @param request 수정된 제목이랑 내용,카테고리를 받아서 넘김
     */
    @Override
    public void updatePost(Integer postId, PostSaveRequestDto request) {
        Post existingPost = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        existingPost.updateDto(request);
        postRepository.save(existingPost);
    }

    @Override
    public void deletePost(Integer post_id) {

    }

    @Override
    public void getPostInfo(Integer post_id) {

    }

    public boolean checkWriterEquals(String currentUsername,Integer post_id){
        Post existingPost = postRepository.findById(post_id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        String writerUsername = existingPost.getWriter_username();
        return writerUsername.equals(currentUsername);
    }
}
