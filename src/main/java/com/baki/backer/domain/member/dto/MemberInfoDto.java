package com.baki.backer.domain.member.dto;

import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MemberInfoDto {

    private Long id;
    private String username;
    private MemberRole user_role;
    private String name;
    private LocalDateTime createDate;
    // 엔티티 -> DTO 변환용 생성자
    public MemberInfoDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.user_role = member.getUser_role();
        this.name = member.getName();
        this.createDate = member.getCreateDate();
    }
}
