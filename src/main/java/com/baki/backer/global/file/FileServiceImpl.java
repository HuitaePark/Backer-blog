package com.baki.backer.global.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl {

    @Value("$(file.dir)")
    private String fileDir;
}
