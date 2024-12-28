package com.baki.backer.domain.comment;

import com.baki.backer.domain.post.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer comment_id;

    @Column(name ="fk_member_id",nullable = false)
    private Integer writer_id;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "fk_post_id",nullable = false)
    private Integer post_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_post_id")
    private Post post;
}
