package com.mz_test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import com.mz_test.exception.MemberNotFoundException;
import com.mz_test.fixture.MemberFixture;
import com.mz_test.fixture.ProfileFixture;
import com.mz_test.repository.MemberRepository;
import com.mz_test.repository.ProfileRepository;
import com.mz_test.request.CreateMemberRequest;
import com.mz_test.request.CreateProfileRequest;
import com.mz_test.response.MemberDetailResponse;
import com.mz_test.response.MemberResponse;
import com.mz_test.response.ProfileResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Slf4j
@Transactional
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Commit
    @DisplayName("올바른 회원 정보가 주어지면 회원이 생성 된다")
    @Test
    void createTest() throws Exception {

        CreateProfileRequest createProfileRequest = ProfileFixture.createProfileRequest();

        CreateMemberRequest createMemberRequest = MemberFixture.createMemberRequest(createProfileRequest);


        mockMvc
                .perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberRequest)))
                .andExpect(status().is(201));


        Member savedMember = memberRepository.findByLoginId(createMemberRequest.getLoginId()).orElseThrow(MemberNotFoundException::new);
        assertNotNull(savedMember);
        assertEquals(createMemberRequest.getLoginId(), savedMember.getLoginId());
        assertEquals(createMemberRequest.getName(), savedMember.getName());
        assertNotEquals(createMemberRequest.getPassword(), savedMember.getPassword()); // 패스워드는 암호화되서 다름

        List<Profile> profiles = profileRepository.findAllByMember(savedMember);
        assertNotNull(profiles);

        Profile savedProfile = profiles.get(0);
        assertEquals(createProfileRequest.getNickname(), savedProfile.getNickname());
        assertEquals(createProfileRequest.getPhone(), savedProfile.getPhone());
        assertEquals(createProfileRequest.getAddress(), savedProfile.getAddress());

    }


    @Test
    @DisplayName("올바르지 않은 회원 정보가 주어지면 LoginIdDuplicateException 를 반환한다")
    void createExceptionTest() throws Exception {

        CreateProfileRequest createProfileRequest = CreateProfileRequest.builder().address("address").phone("010-1111-4554").nickname("nickname").build();

        CreateMemberRequest createMemberRequest1 = CreateMemberRequest.builder().
                                                    createProfileRequest(createProfileRequest).
                                                    loginId("duplicateId").
                                                    name("nameme").
                                                    password("hhihhw123").
                                                    build();

        mockMvc
                .perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMemberRequest1)))
                .andExpect(status().is(201));

        //  동일한 loginId를 가진 두 번째 회원 등록
        mockMvc
                .perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMemberRequest1)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("올바른 회원 정보가 주어지면 회원이 삭제된다.")
    void deleteTest() throws Exception {
        Member member = MemberFixture.idMember("imdelete");
        Profile profile = ProfileFixture.profile(member);
        memberRepository.save(member);
        profileRepository.save(profile);
        mockMvc
                .perform(delete("/api/members/" + member.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));

        Member deleteMember = memberRepository.findByLoginId("imdelete").orElse(null);
        assertNull(deleteMember);
    }


    @Test
    @DisplayName("올바른 페이지 정보와 검색어가 주어지면 Page<MemberResponse>를 반환한다")
    void findAllTest() throws Exception {
        addMembers(50);

        long page = 0;
        long size = 3;
        String searchName = "1";

        MvcResult mvcResult = mockMvc.perform(get("/api/members")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("searchName", searchName)
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        List<MemberResponse> MemberResponseList = listMemberResponseFromJsonString(content);
        assertEquals(size, MemberResponseList.size());
        for (MemberResponse dto : MemberResponseList) {
            log.info("ID : {}", dto.getMemberId());
            log.info("NICKNAME : {}", dto.getNickname());
            log.info("NAME : {}", dto.getName());
        }

    }


    @Test
    @DisplayName("올바른 회원ID 가 주어지면 회원의 정보와 프로필을 반환한다.")
    void findTest() throws Exception {
        addMembers(10);
        long index = 4; // 회원 선택

        // 선택 유저 프로필 5개 등록
        Member member = memberRepository.findById(index).get();
        for (int i = 0; i < 5; i++) {
            Profile profile = ProfileFixture.profile(member, 0);
            profileRepository.save(profile);
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/members/" + index)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        MemberDetailResponse memberDetailResponse = memberDetailResponsefromJsonString(content);

        assertEquals(6, memberDetailResponse.getProfileList().size()); // 임의로 넣은 프로필 5개 + addMember()에서 기본으로 들어가는 프로필 1개
        assertEquals(member.getId(), memberDetailResponse.getMemberId());

    }

    private void addMembers(int count) {
        for (long i = 0; i < count; i++) {
            Member member = MemberFixture.idMember((int)i);
            Profile profile = ProfileFixture.profile(member, i);
            memberRepository.save(member);
            profileRepository.save(profile);
        }
    }
    public MemberDetailResponse memberDetailResponsefromJsonString(String jsonString) throws JsonProcessingException {
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

    public List<MemberResponse>  listMemberResponseFromJsonString(String jsonString) throws JsonProcessingException {
        List<MemberResponse> memberResponses = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(jsonString);
        JsonNode contentNode = jsonNode.path("data").path("content");

        for (JsonNode memberNode : contentNode) {
            MemberResponse memberResponse = objectMapper.treeToValue(memberNode, MemberResponse.class);
            memberResponses.add(memberResponse);
        }
        return memberResponses;
    }

}
