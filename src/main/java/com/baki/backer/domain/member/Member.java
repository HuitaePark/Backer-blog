package com.baki.backer.domain.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime crate_date;

    @Enumerated(EnumType.STRING)
    private MemberRole user_role;

    public Member(LocalDateTime crate_date, Integer id, String name, String password, String username,MemberRole user_role) {
        this.crate_date = crate_date;
        this.id = id;
        this.name = name;
        this.password = password;
        this.username = username;
        this.user_role = user_role;
    }


}
