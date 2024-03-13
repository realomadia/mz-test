package com.mz_test.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
public class MemberDetailResponse {
    private Long memberId;
    private String loginId;
    private String name;
    private List<ProfileResponse> profileList;

    public MemberDetailResponse(Member member, List<Profile> profiles) {
        this.memberId = member.getId();
        this.loginId = member.getLoginId();
        this.name = member.getName();
        this.profileList = profiles.stream().map(ProfileResponse::new).collect(Collectors.toList());
    }

}
