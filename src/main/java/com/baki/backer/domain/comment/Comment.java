package com.baki.backer.domain.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Comment {
    Integer id;
    Integer author_id;
    String content;
    LocalDateTime create_date;

    public Comment(Integer author_id, String content, LocalDateTime create_date, Integer id) {
        this.author_id = author_id;
        this.content = content;
        this.create_date = create_date;
        this.id = id;
    }

}