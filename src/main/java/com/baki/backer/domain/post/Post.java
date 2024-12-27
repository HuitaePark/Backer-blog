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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private Integer fk_member_id;
    @Enumerated(EnumType.STRING)
    private Category category_id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;

    public Post(Integer fk_member_id, String content, Integer id, String title) {
        this.fk_member_id = fk_member_id;
        this.content = content;
        this.id = id;
        this.title = title;
    }
}
