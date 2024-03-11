package com.mz_test.mz_test.domain.api.member.entity;

import com.mz_test.mz_test.domain.api.member.dto.request.AddMemberDto;
import com.mz_test.mz_test.domain.api.member.dto.response.MemberDto;
import com.mz_test.mz_test.domain.api.profile.dto.request.AddProfileDto;
import com.mz_test.mz_test.domain.api.profile.dto.request.DeleteProfileDto;
import com.mz_test.mz_test.domain.api.profile.entity.Profile;
import com.mz_test.mz_test.global.config.exception.CustomException;
import com.mz_test.mz_test.global.config.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "salt", nullable = false)
    private String salt;

    @Column(name = "role", nullable = false)
    private String role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Profile> profiles = new ArrayList<>();

    @Builder
    public Member(String loginId, String name, String password, String salt) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.salt = salt;
        this.role = "member";
    }

    public static Member createWithProfile(AddMemberDto addMemberDto) throws DataIntegrityViolationException {

        // 프로필이 null이거나 비어있으면 예외처리
        if (addMemberDto.getAddProfileDto() == null) {
            throw new CustomException("REQUIRED PROFILE IS NOT FOUND", ErrorCode.REQUIRED_PROFILE);
        }

        Member member = Member.builder()
                .loginId(addMemberDto.getLoginId())
                .name(addMemberDto.getName())
                .password(addMemberDto.getPassword())
                .salt(addMemberDto.getSalt())
                .build();

        Profile profile = Profile.builder()
                .nickname(addMemberDto.getAddProfileDto().getNickname())
                .phone(addMemberDto.getAddProfileDto().getPhone())
                .address(addMemberDto.getAddProfileDto().getAddress())
                .member(member)
                .isMain(true)
                .build();

        member.getProfiles().add(profile);

        return member;
    }

    public void addProfile(AddProfileDto addProfileDto) {
        Profile profile = Profile.builder()
                .nickname(addProfileDto.getNickname())
                .phone(addProfileDto.getPhone())
                .address(addProfileDto.getAddress())
                .member(this)
                .isMain(false)
                .build();

        this.profiles.add(profile);
    }
    public void deleteProfile(DeleteProfileDto deleteProfileDto) {
        // 프로필이 하나뿐인 경우 삭제 불가능
        if (profiles.size() <= 1) {
            throw new CustomException("CANNOT_DELETE_THE_ONLY_PROFILE", ErrorCode.PROFILE_DELETE_FAILED);
        }

        // 삭제할 프로필 찾기
        Profile profileToDelete = profiles.stream()
                .filter(profile -> profile.getProfileId().equals(deleteProfileDto.getProfileId()))
                .findAny()
                .orElseThrow(() -> new CustomException("PROFILE_NOT_FOUND ID: " + deleteProfileDto.getProfileId(), ErrorCode.PROFILE_NOT_FOUND));

        // 메인 프로필인 경우
        if (profileToDelete.getIsMain() == 1) {
            // 다음 프로필이 있으면 다음 프로필을 메인 프로필로 변경
            Profile nextProfile = profiles.stream()
                    .filter(profile -> !profile.getProfileId().equals(deleteProfileDto.getProfileId()))
                    .findAny()
                    .orElse(null);

            if (nextProfile != null) {
                nextProfile.settingMainProfile();
            }
        }
        // 해당 프로필 삭제
        profiles.remove(profileToDelete);
        profileToDelete.removeProfile();
    }


    // 관리자로 변경
    public void promoteToAdmin() {
        this.role = "admin";
    }

    // 일반 멤버로 변경
    public void demoteToMember() {
        this.role = "member";
    }

}
