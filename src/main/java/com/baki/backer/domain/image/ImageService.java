package com.baki.backer.domain.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    Long uploadProfileImage(MultipartFile file, Long memberId) throws Exception;
    Long uploadPostImage(MultipartFile file, Long memberId) throws Exception;
    String getPublicImgUrl(Long imgId,Long memberId) throws Exception;
    MultipartFile downloadImg(Long imageId,Long memberId) throws Exception;
    void deleteImg(Image image) throws Exception;
}
