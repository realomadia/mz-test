package com.mz_test.mz_test.domain.api.profile.entity;

import com.mz_test.mz_test.domain.api.member.entity.Member;
import com.mz_test.mz_test.domain.api.profile.dto.request.EditProfileDto;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "TB_PROFILE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "is_main")
    private int isMain;


    @Builder
    public Profile(String nickname, String phone, String address, Member member, boolean isMain) {
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
        this.member = member;
        if(isMain){
            this.isMain = 1;
        }else{
            this.isMain = 0;
        }
    }
    public void editProfile(EditProfileDto editProfileDto) {
            this.nickname = editProfileDto.getNickname();
            this.phone = editProfileDto.getPhone();
            this.address = editProfileDto.getAddress();
    }
    // 메인 프로필로 변경
    public void settingMainProfile() {
        this.isMain = 1;
    }

}
