package com.baki.backer.global.util;

import com.baki.backer.global.common.ApiResponseDto;
import com.baki.backer.global.error.ErrorResponse;

public class ResponseUtil {

    // 요청 성공인 경우
    public static <T> ApiResponseDto<T> ok(T response){
        return ApiResponseDto.<T>builder()
                .success(true)
                .response(response)
                .build();
    }
    //에러인 경우
    public static <T> ApiResponseDto<T> error(ErrorResponse errorResponse){
        return ApiResponseDto.<T>builder()
                .success(false)
                .errorResponse(errorResponse)
                .build();
    }
}
