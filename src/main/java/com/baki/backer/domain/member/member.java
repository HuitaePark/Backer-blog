package com.baki.backer.domain.member;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class member {
    Integer id;
    String username;
    String password;
    String name;
    LocalDateTime crate_date;

    public member(LocalDateTime crate_date, Integer id, String name, String password, String username) {
        this.crate_date = crate_date;
        this.id = id;
        this.name = name;
        this.password = password;
        this.username = username;
    }

    public void setCrate_date(LocalDateTime crate_date) {
        this.crate_date = crate_date;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
