package com.baki.backer.domain.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;
    private String name;
    private LocalDateTime crate_date;

    private MemberRole role;

    public Member(LocalDateTime crate_date, Integer id, String name, String password, String username) {
        this.crate_date = crate_date;
        this.id = id;
        this.name = name;
        this.password = password;
        this.username = username;
    }


}
