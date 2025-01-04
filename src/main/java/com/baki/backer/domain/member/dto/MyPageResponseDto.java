package com.baki.backer.domain.member.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MyPageResponseDto(
        List<PostResponseDto> posts,
        List<CommentResponseDto> comments
) {
}
