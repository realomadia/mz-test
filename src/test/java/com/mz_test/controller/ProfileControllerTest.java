package com.mz_test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import com.mz_test.fixture.MemberFixture;
import com.mz_test.fixture.ProfileFixture;
import com.mz_test.repository.MemberRepository;
import com.mz_test.repository.ProfileRepository;
import com.mz_test.request.CreateProfileRequest;
import com.mz_test.request.EditProfileRequest;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("올바른 프로필 정보가 주어지면 프로필이 생성된다.")
    void createTest() throws Exception {

        CreateProfileRequest createProfileRequest = ProfileFixture.createProfileRequest();
        Member member = MemberFixture.idMember();
        memberRepository.save(member);

        mockMvc.perform(post("/api/members/" + member.getId() + "/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProfileRequest)))
                .andExpect(status().is(201));

    }

    @Test
    @DisplayName("올바른 프로필 정보가 주어지면 프로필이 수정된다.")
    void editTest() throws Exception {
        Member member = memberRepository.save(MemberFixture.idMember());
        Profile profile = profileRepository.save(ProfileFixture.profile(member,1));
        // 프로필 수정전 수정할 프로필 선택

        EditProfileRequest editProfileRequest = EditProfileRequest.builder()
                .nickname("updatedNickname")
                .address("updatedAddress")
                .phone("010-1111-1111")
                .build();

        mockMvc.perform(put("/api/members/"+member.getId()+"/profiles/"+profile.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().is(201));


        Profile updatedProfile = profileRepository.findById(profile.getId()).get();
        // 수정됬는지 확인
        assertEquals("updatedNickname", updatedProfile.getNickname());
        assertEquals("updatedAddress", updatedProfile.getAddress());
        assertEquals("010-1111-1111", updatedProfile.getPhone());
    }


    @Test
    @DisplayName("회원 프로필이 한개일시 삭제할수 없다.")
    void deleteExceptionTest() throws Exception {
        Member member = MemberFixture.idMember("notDelete");
        memberRepository.save(member);
        Profile profile = ProfileFixture.profile(member, 1);
        profileRepository.save(profile);

        mockMvc.perform(delete("/api/members/"+ member.getId() +"/profiles/" + profile.getId())// 메인 프로필이 한개밖에 없기 때문에 실패
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.message").value("프로필은 최소 한개 가지고 있어야 합니다."));
    }


    @Test
    @DisplayName("회원 프로필이 두개이상이고 메인프로필이 주어지면 삭제후 남은 프로필하나가 메인 프로필로 변경된다.")
    void deleteTest1() throws Exception {
        Member member = MemberFixture.idMember();
        memberRepository.save(member);
        Profile profile1 = ProfileFixture.profile(member, 1);
        Profile profile2 = ProfileFixture.profile(member, 0);
        profileRepository.save(profile1);
        profileRepository.save(profile2);

        mockMvc.perform(delete("/api/members/"+ member.getId() +"/profiles/" + profile1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));

    }


    @Test
    @DisplayName("회원 프로필이 두개이고 메인프로필이 아닌 프로필이 주어지면 삭제된다.")
    void deleteTest2() throws Exception {
        Member member = MemberFixture.idMember();
        memberRepository.save(member);
        Profile profile1 = ProfileFixture.profile(member, 1);
        Profile profile2 = ProfileFixture.profile(member, 0);
        profileRepository.save(profile1);
        profileRepository.save(profile2);

        mockMvc.perform(delete("/api/members/"+ member.getId() +"/profiles/" + profile2.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));

    }

}