package com.baki.backer.domain.member;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username",nullable = false, unique = true)
    private String username;

    @Column(name = "password",nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private MemberRole user_role;

    @Column(name = "name",nullable = false)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

}
