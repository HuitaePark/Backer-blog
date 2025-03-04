package com.baki.backer.global.common;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data

public class SuccessMessageDto {
    private int status;
    private String message;

    @Builder
    public SuccessMessageDto(int status, String message) {
        this.status = status;
        this.message = message;
    }
    public static SuccessMessageDto of(HttpStatus status, String message){
        return  SuccessMessageDto.builder()
                .status(status.value())
                .message(message)
                .build();
    }
}
