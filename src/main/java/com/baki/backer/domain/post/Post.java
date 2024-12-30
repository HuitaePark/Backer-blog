package com.baki.backer.domain.post;

import com.baki.backer.domain.comment.Comment;
import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.post.dto.PostSaveRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

    @Column(name= "fk_member_name",nullable = false)
    private String writer_name;

    @Enumerated(EnumType.STRING)
    private Category category_id;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "content",nullable = false,columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime create_Date;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_member_id", nullable = false)
    private Member member;

    public void updateDto(PostSaveRequestDto request) {
        this.title = request.title();
        this.content = request.content();
        this.category_id = request.category_id();
    }

}
