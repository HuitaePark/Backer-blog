package com.baki.backer.domain.member.dto;

import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestDto {

    @NotBlank(message = "로그인 아이디가 비어있습니다.")
    private String username;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "이름이 비어있습니다.")
    private String name;

    public Member toEntity(){
        return Member.builder()
                .username(this.username)
                .password(this.password)
                .name(this.name)
                .user_role(MemberRole.USER)
                .build();
    }
}
