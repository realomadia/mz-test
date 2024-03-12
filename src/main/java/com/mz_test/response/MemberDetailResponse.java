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
    public static MemberDetailResponse fromJsonString(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        JsonNode dataNode = jsonNode.path("data");
        Long memberId = dataNode.path("memberId").asLong();
        String loginId = dataNode.path("loginId").asText();
        String name = dataNode.path("name").asText();

        JsonNode profileListNode = dataNode.path("profileList");
        List<ProfileResponse> profileList = new ArrayList<>();
        for (JsonNode profileNode : profileListNode) {
            ProfileResponse profileResponse = objectMapper.treeToValue(profileNode, ProfileResponse.class);
            profileList.add(profileResponse);
        }

        MemberDetailResponse memberDetailResponse = new MemberDetailResponse();
        memberDetailResponse.setMemberId(memberId);
        memberDetailResponse.setLoginId(loginId);
        memberDetailResponse.setName(name);
        memberDetailResponse.setProfileList(profileList);

        return memberDetailResponse;
    }

}
