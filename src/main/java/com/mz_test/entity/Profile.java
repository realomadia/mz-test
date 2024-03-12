package com.mz_test.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "is_main")
    private int isMain;

    public Profile(Long id, Member member, String nickname, String phone, String address, int isMain) {
        this.id = id;
        this.member = member;
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
        this.isMain = isMain;
    }

    @Builder
    public Profile(String nickname, String phone, String address, Member member, int isMain) {
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
        this.member = member;
        this.isMain = isMain;
    }

    public void modify(String nickname, String phone, String address) {
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
    }
    public void changeMain(){
        this.isMain = 1;
    }
}
