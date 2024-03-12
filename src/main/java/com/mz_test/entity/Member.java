package com.mz_test.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "salt", nullable = false)
    private String salt;

    public Member(Long id, String loginId, String name, String password, String salt) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.salt = salt;
    }

    @Builder
    public Member(String loginId, String name, String password, String salt) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.salt = salt;
    }

}
