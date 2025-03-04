package com.baki.backer.global.error.exception;

import com.baki.backer.global.common.ApiResponseDto;
import com.baki.backer.global.error.ErrorResponse;
import com.baki.backer.global.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<ErrorResponse>> methodValidException(MethodArgumentNotValidException e){
        ErrorResponse errorResponse = ErrorResponse.of(e.getBindingResult());
        log.error(errorResponse.getMessage());
        return ResponseEntity.badRequest().body(ResponseUtil.error(errorResponse));
    }
}
