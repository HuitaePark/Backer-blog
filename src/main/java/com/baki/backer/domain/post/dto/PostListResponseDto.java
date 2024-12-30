package com.baki.backer.domain.post.dto;

import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.post.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDto {
    Integer post_id;
    String title;
    String content;
    String name;
    LocalDateTime create_date;

    public static PostListResponseDto fromEntity(Post post) {
        return new PostListResponseDto(
                post.getPost_id(),
                post.getTitle(),
                post.getContent(),
                post.getMember().getName(),
                post.getCreate_Date()
        );
    }
}
