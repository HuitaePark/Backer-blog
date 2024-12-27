package com.baki.backer.global.file;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.processing.FilerException;

public interface FileService {

    //저장된 파일 경로 반환
    String saveFile(MultipartFile multipartFile) throws FilerException;

    void deleteFile(String filePath);
}
