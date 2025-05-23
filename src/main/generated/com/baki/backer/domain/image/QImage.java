package com.baki.backer.domain.image;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QImage is a Querydsl query type for Image
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QImage extends EntityPathBase<Image> {

    private static final long serialVersionUID = 195337812L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QImage image = new QImage("image");

    public final StringPath accessUri = createString("accessUri");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ImageType> imageType = createEnum("imageType", ImageType.class);

    public final StringPath imgUri = createString("imgUri");

    public final com.baki.backer.domain.member.QMember member;

    public final StringPath parId = createString("parId");

    public QImage(String variable) {
        this(Image.class, forVariable(variable), INITS);
    }

    public QImage(Path<? extends Image> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QImage(PathMetadata metadata, PathInits inits) {
        this(Image.class, metadata, inits);
    }

    public QImage(Class<? extends Image> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.baki.backer.domain.member.QMember(forProperty("member")) : null;
    }

}

