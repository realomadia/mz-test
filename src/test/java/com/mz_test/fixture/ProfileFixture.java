package com.mz_test.fixture;

import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import com.mz_test.request.CreateMemberRequest;
import com.mz_test.request.CreateProfileRequest;

public class ProfileFixture {

    public static Profile profile(Long id) {

        return new Profile(id,MemberFixture.idMember(), "megazone","010-1234-5678", "seoul", 1);
    }

    public static Profile profile(Member member, Long id) {
        return new Profile(id, member, "megazone","010-1234-5678", "seoul", 1);
    }

    public static Profile profile(Member member) {
        return Profile.builder().member(member).nickname("testNickname").phone("010-1144-1144").isMain(0).address("texas").build();
    }
    public static Profile mainProfile(Member member) {
        return Profile.builder().member(member).nickname("testNickname").phone("010-1144-1144").isMain(1).address("texas").build();
    }

    public static Profile profile(Member member, int isMain) {
        return Profile.builder()
                .nickname("meganickname")
                .address("address")
                .phone("010-2221-1134")
                .isMain(isMain)
                .member(member)
                .build();
    }

    public static CreateProfileRequest createProfileRequest() {
        return CreateProfileRequest.builder()
                .nickname("megazone")
                .phone("010-7457-4747")
                .address("seoul")
                .build();
    }

}
