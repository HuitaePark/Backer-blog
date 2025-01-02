package com.baki.backer.domain.post;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -193987654L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final EnumPath<Category> category_id = createEnum("category_id", Category.class);

    public final ListPath<com.baki.backer.domain.comment.Comment, com.baki.backer.domain.comment.QComment> comments = this.<com.baki.backer.domain.comment.Comment, com.baki.backer.domain.comment.QComment>createList("comments", com.baki.backer.domain.comment.Comment.class, com.baki.backer.domain.comment.QComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> create_Date = createDateTime("create_Date", java.time.LocalDateTime.class);

    public final com.baki.backer.domain.member.QMember member;

    public final NumberPath<Integer> post_id = createNumber("post_id", Integer.class);

    public final StringPath title = createString("title");

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.baki.backer.domain.member.QMember(forProperty("member")) : null;
    }

}

