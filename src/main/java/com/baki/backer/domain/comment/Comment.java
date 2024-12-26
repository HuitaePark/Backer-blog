package com.baki.backer.domain.comment;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer fk_member_id;
    @Column(nullable = false)
    private String content;
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime create_date;
    @Column(nullable = false)
    private Integer fk_post_id;

    public Comment(Integer fk_member_id, String content, LocalDateTime create_date, Integer id, Integer fk_post_id) {
        this.fk_member_id = fk_member_id;
        this.content = content;
        this.create_date = LocalDateTime.now();
        this.id = id;
        this.fk_post_id = fk_post_id;
    }

}
