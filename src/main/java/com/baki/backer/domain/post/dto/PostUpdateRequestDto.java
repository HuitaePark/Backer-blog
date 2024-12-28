package com.baki.backer.domain.post.dto;

import com.baki.backer.domain.post.Category;
import jakarta.validation.constraints.NotBlank;

public record PostUpdateRequestDto(@NotBlank(message = "제목을 입력해야 합니다.") String title,
                                   @NotBlank(message = "내용을 입력해야 합니다.") String content,
                                   @NotBlank(message = "카테고리를 선택해야 합니다.") Category category_id) {
}