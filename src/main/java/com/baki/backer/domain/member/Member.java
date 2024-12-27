package com.baki.backer.domain.member;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username",nullable = false, unique = true)
    private String username;

    @Column(name = "password",nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private MemberRole user_role;

    @Column(name = "name",nullable = false)
    private String name;
}
