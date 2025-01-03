package com.baki.backer.domain.post.dto;

import com.baki.backer.domain.comment.dto.CommentRequestDto;
import com.baki.backer.domain.post.Category;
import com.baki.backer.domain.post.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DetailPostResponseDto{
    private Long id;
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
        this.name = post.getMember().getName();
        this.create_date=post.getCreate_Date();
        this.comments = post.getComments().stream()
                .map(CommentRequestDto::new)
                .collect(Collectors.toList());
    }
}
