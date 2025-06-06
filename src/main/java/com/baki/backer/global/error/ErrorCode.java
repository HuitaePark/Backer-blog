package com.baki.backer.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST,400 , "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED,405 , "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,500 , "서버 에러가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 엔티티입니다.");


    private final String message;

    private final int code;
    private final HttpStatus status;

    ErrorCode(final HttpStatus status, final int code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}