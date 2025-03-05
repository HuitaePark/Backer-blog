package com.baki.backer.domain.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    String username;
    Long userId;
    String message;
}
