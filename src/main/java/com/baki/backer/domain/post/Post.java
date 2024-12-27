package com.baki.backer.domain.post;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer post_id;

    @Column(name="fk_member_username",nullable = false)
    private String writer_username;

    @Enumerated(EnumType.STRING)
    private Category category_id;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "content",nullable = false)
    private String content;
}
