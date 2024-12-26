package com.baki.backer.domain.post;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime create_date;

    public Post(Integer fk_member_id, String content, LocalDateTime create_date, Integer id, String title) {
        this.fk_member_id = fk_member_id;
        this.content = content;
        this.create_date = LocalDateTime.now();
        this.id = id;
        this.title = title;
    }
}
