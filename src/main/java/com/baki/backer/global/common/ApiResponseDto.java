package com.baki.backer.global.common;

import com.baki.backer.global.error.ErrorResponse;
import lombok.Builder;
import lombok.Getter;


@Getter
public class ApiResponseDto<T> {
    private boolean success;
    private T response;
    private ErrorResponse errorResponse;

    @Builder
    private ApiResponseDto(boolean success, T response, ErrorResponse errorResponse) {
        this.success = success;
        this.response = response;
        this.errorResponse = errorResponse;
    }
}
