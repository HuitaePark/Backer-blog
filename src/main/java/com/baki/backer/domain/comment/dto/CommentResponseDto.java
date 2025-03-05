package com.baki.backer.domain.comment.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private Long postId;
    private String content;
    private String author;
    private LocalDateTime createDate;


}
