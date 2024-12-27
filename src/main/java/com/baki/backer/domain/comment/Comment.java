package com.baki.backer.domain.comment;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer fk_member_id;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Integer fk_post_id;


    public Comment(Integer fk_member_id, String content, Integer id, Integer fk_post_id) {
        this.fk_member_id = fk_member_id;
        this.content = content;
        this.id = id;
        this.fk_post_id = fk_post_id;
    }

}
