package com.baki.backer.domain.image;


import com.baki.backer.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_member_id")
    private Member member;

    @Column(length = 1000)
    private String accessUri;

    private String imgUri;

    private String parId;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    public void updateAccessUri(String accessUri) {
        this.accessUri = accessUri;
    }

    public void updateParId(String parId) {
        this.parId = parId;
    }

    public void updateImageType(ImageType imageType) {
        this.imageType = imageType;
    }
}
