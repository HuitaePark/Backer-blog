package com.baki.backer.domain.post.dto;

import com.baki.backer.domain.post.Category;
import com.baki.backer.domain.post.Post;
import jakarta.validation.constraints.NotBlank;

public record PostSaveRequestDto(@NotBlank(message = "제목을 입력해야 합니다.") String title,
                                 @NotBlank(message = "내용을 입력해야 합니다.") String content,
                                 @NotBlank(message = "카테고리를 선택해야 합니다.") Category category_id) {
    public Post toEntity(){
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .category_id(this.category_id)
                .build();
    }
}
