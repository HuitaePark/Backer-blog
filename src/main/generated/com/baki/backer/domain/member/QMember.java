package com.baki.backer.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 870720762L;

    public static final QMember member = new QMember("member1");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final ListPath<com.baki.backer.domain.image.Image, com.baki.backer.domain.image.QImage> profilePictures = this.<com.baki.backer.domain.image.Image, com.baki.backer.domain.image.QImage>createList("profilePictures", com.baki.backer.domain.image.Image.class, com.baki.backer.domain.image.QImage.class, PathInits.DIRECT2);

    public final EnumPath<MemberRole> user_role = createEnum("user_role", MemberRole.class);

    public final StringPath username = createString("username");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

