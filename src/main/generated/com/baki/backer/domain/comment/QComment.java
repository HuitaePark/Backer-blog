package com.baki.backer.domain.comment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = -1704255716L;

    public static final QComment comment = new QComment("comment");

    public final NumberPath<Integer> comment_id = createNumber("comment_id", Integer.class);

    public final StringPath content = createString("content");

    public final NumberPath<Integer> post_id = createNumber("post_id", Integer.class);

    public final NumberPath<Integer> writer_id = createNumber("writer_id", Integer.class);

    public QComment(String variable) {
        super(Comment.class, forVariable(variable));
    }

    public QComment(Path<? extends Comment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QComment(PathMetadata metadata) {
        super(Comment.class, metadata);
    }

}

