package com.baki.backer.domain.post;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Post {
    Integer id;
    Integer author_id;
    Category category_id;
    String title;
    String content;
    LocalDateTime create_date;

    public Post(Integer author_id, String content, LocalDateTime create_date, Integer id, String title) {
        this.author_id = author_id;
        this.content = content;
        this.create_date = create_date;
        this.id = id;
        this.title = title;
    }
}