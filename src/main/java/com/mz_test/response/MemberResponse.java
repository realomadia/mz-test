package com.mz_test.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@ToString
@Getter
@Setter
@NoArgsConstructor
public class MemberResponse {

    private Long memberId;
    private String loginId;
    private String name;
    private Long profileId;
    private String nickname;
    private String phone;
    private String address;

    public MemberResponse(Profile profile) {
        this.memberId = profile.getMember().getId();
        this.loginId = profile.getMember().getLoginId();
        this.name = profile.getMember().getName();
        this.profileId = profile.getId();
        this.nickname = profile.getNickname();
        this.phone = profile.getPhone();
        this.address = profile.getAddress();
    }


}