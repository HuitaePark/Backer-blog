package com.baki.backer.domain.post;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -193987654L;

    public static final QPost post = new QPost("post");

    public final EnumPath<Category> category_id = createEnum("category_id", Category.class);

    public final StringPath content = createString("content");

    public final NumberPath<Integer> post_id = createNumber("post_id", Integer.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> writer_id = createNumber("writer_id", Integer.class);

    public final StringPath writer_username = createString("writer_username");

    public QPost(String variable) {
        super(Post.class, forVariable(variable));
    }

    public QPost(Path<? extends Post> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPost(PathMetadata metadata) {
        super(Post.class, metadata);
    }

}

