package com.baki.backer.domain.post.dto;

import com.baki.backer.domain.comment.dto.CommentRequestDto;
import com.baki.backer.domain.post.Category;
import com.baki.backer.domain.post.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DetailPostResponseDto{
    private Integer id;
    private String title;
    private String content;
    private String name;
    private Category category_id;
    private LocalDateTime create_date;
    private List<CommentRequestDto> comments;

    public DetailPostResponseDto(Post post) {
        this.category_id = post.getCategory_id();
        this.content = post.getContent();
        this.id = post.getPost_id();
        this.title = post.getTitle();
        this.name = post.getWriter_name();
        this.create_date=post.getCreate_Date();
        this.comments = post.getComments().stream()
                .map(CommentRequestDto::new)
                .collect(Collectors.toList());
    }
}
