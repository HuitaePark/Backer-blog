package com.baki.backer.domain.post.dto;

import com.baki.backer.domain.post.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostListResponseDto {

    // 순서 맞춰서 필드를 선언(굳이 순서는 상관 없지만, 가독성 위해 맞춰줍니다)
    private LocalDateTime create_date;
    private Long post_id;
    private String name;
    private String title;
    private String content;

    /**
     * Querydsl의 Projections.constructor(...) 로 매핑 시,
     * [LocalDateTime, Integer, String, String, String] 이 순서대로 들어오는 생성자
     */

    public PostListResponseDto(LocalDateTime create_date,
                               Long post_id,
                               String name,
                               String title,
                               String content) {
        this.create_date = create_date;
        this.post_id = post_id;
        this.name = name;
        this.title = title;
        this.content = content;
    }

    /**
     * 엔티티 -> DTO 변환용 정적 메서드
     * (직접 DTO를 생성해야 할 때 사용)
     */
    public static PostListResponseDto fromEntity(Post post) {
        return new PostListResponseDto(
                post.getCreate_Date(),          // LocalDateTime
                post.getPost_id(),             // Integer (엔티티가 int/Integer라 가정)
                post.getMember().getName(),    // String
                post.getTitle(),               // String
                post.getContent()              // String
        );
    }
}
