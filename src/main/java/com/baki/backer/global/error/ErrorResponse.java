package com.baki.backer.global.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.jdt.internal.compiler.apt.model.ErrorTypeImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import javax.lang.model.type.ErrorType;

@Getter

public class ErrorResponse {

    private String message;
    private int status;

    @Builder
    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }
    public static ErrorResponse of(ErrorCode errorCode){
        return ErrorResponse.builder()
                .status(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
    public static ErrorResponse of(HttpStatus status,String message){
        return  ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .build();
    }
    public static ErrorResponse of(BindingResult bindingResult){
        String message = " ";

        if (bindingResult.hasErrors()){
            message = bindingResult.getAllErrors().get(0).getDefaultMessage();
        }
        return ErrorResponse.of(HttpStatus.BAD_REQUEST,message);
    }
}