package com.baki.backer.domain.comment;

import com.baki.backer.domain.comment.dto.CommentRequestDto;
import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_member_id", nullable = false)
    private Member member;

    @Column(name = "content",nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_post_id",nullable = false)
    private Post post;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate;

    public void updateDto(CommentRequestDto commentRequestDto) {
        this.content = content;
    }
}
