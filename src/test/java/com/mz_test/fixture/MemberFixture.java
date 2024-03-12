package com.mz_test.fixture;

import com.mz_test.entity.Member;
import com.mz_test.request.CreateMemberRequest;
import com.mz_test.request.CreateProfileRequest;

public class MemberFixture {

    public static Member idMember() {
        return new Member(1L, "email@domain.com", "name", "1234", "saltbae");
    }
    public static Member idMember(Long id) {
        return new Member(id, "email@domain.com", "name", "1234", "saltbae");
    }
    public static Member idMember(int count) {
        return new Member((long)count, "email@domain.com"+count, "name"+count, "1234"+count, "saltbae"+count);
    }

    public static Member idMember(String loginId) {
        return new Member(loginId, "name", "1234", "saltbae");
    }



    public static Member idMember(CreateMemberRequest memberRequest) {
        return new Member(1L, memberRequest.getLoginId(), memberRequest.getName(), memberRequest.getPassword(), "saltbae");
    }

    public static CreateMemberRequest createMemberRequest(CreateProfileRequest profileRequest) {
        return CreateMemberRequest.builder()
                .name("mzd")
                .loginId("mzd840")
                .password("mzd221155")
                .createProfileRequest(profileRequest)
                .build();
    }

}
