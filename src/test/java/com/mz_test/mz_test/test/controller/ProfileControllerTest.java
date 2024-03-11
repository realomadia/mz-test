package com.mz_test.mz_test.test.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz_test.mz_test.domain.api.member.dto.request.AddMemberDto;
import com.mz_test.mz_test.domain.api.member.dto.request.LoginDto;
import com.mz_test.mz_test.domain.api.member.entity.Member;
import com.mz_test.mz_test.domain.api.member.repository.MemberRepository;
import com.mz_test.mz_test.domain.api.profile.dto.request.AddProfileDto;
import com.mz_test.mz_test.domain.api.profile.dto.request.DeleteProfileDto;
import com.mz_test.mz_test.domain.api.profile.dto.request.EditProfileDto;
import com.mz_test.mz_test.domain.api.profile.entity.Profile;
import com.mz_test.mz_test.domain.api.profile.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;
    private String token; // api 인증용 토큰

    // 테스트 전에 로그인후 토큰을 리턴받아 전역 변수에 저장
    @BeforeEach
    void setup() throws Exception {
        token = loginAndGetToken();
    }

    /**
     * 로그인 후 토큰 저장
     */
    private String loginAndGetToken() throws Exception {

        AddProfileDto addProfileDto = new AddProfileDto();
        addProfileDto.setAddress(null); // 주소는 null가능
        addProfileDto.setNickname("TEST NICKNAME");
        addProfileDto.setPhone("010-4514-4840");

        AddMemberDto addMemberDto = new AddMemberDto();
        addMemberDto.setLoginId("testMember");
        addMemberDto.setName("Test Member");
        addMemberDto.setPassword("password");
        addMemberDto.setAddProfileDto(addProfileDto);


        mockMvc
                .perform(post("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addMemberDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));

        LoginDto loginDto = new LoginDto();
        loginDto.setId("testMember");
        loginDto.setPassword("password");

        MvcResult result = mockMvc
                .perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(token);

        String dataValue = jsonNode.get("data").asText();
        log.info("Token : {}", dataValue);

        return dataValue;
    }

    /**
     * 프로필 추가 성공 테스트
     */
    @Test
//    @Rollback(value = false)
    void addProfile_success() throws Exception {


        AddProfileDto addProfileDto = new AddProfileDto();
        addProfileDto.setNickname("lomadia");
        addProfileDto.setPhone("010-4444-4840");
        addProfileDto.setAddress("IDK");


        mockMvc.perform(post("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(addProfileDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));

    }

    /**
     * 프로필 수정 테스트
     */
    @Test
//    @Rollback(value = false)
    void editProfile_success() throws Exception {

        // 프로필 수정전 수정할 프로필 선택
        Member member = memberRepository.findByLoginId("testMember").orElseThrow(() -> new RuntimeException("MEMBER_NOT_FOUND"));
        Long targetProfileId = member.getProfiles().get(0).getProfileId();
        EditProfileDto editProfileDto = new EditProfileDto();
        editProfileDto.setProfileId(targetProfileId);
        editProfileDto.setNickname("updatedNickname");
        editProfileDto.setAddress("updatedAddress");
        editProfileDto.setPhone("010-1111-1111");

        // When
        mockMvc.perform(put("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(editProfileDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));

        // 수정됬는지 확인
        assertEquals("updatedNickname", member.getProfiles().get(0).getNickname());
        assertEquals("updatedAddress", member.getProfiles().get(0).getAddress());
        assertEquals("010-1111-1111", member.getProfiles().get(0).getPhone());
    }

    /**
     * 프로필 삭제 실패 테스트(프로필이 한개 일때)
     */
    @Test
//    @Rollback(value = false)
    void deleteProfile_failed() throws Exception {
        // 프로필 삭제전 수정할 프로필 선택
        Member member = memberRepository.findByLoginId("testMember").orElseThrow(() -> new RuntimeException("MEMBER_NOT_FOUND"));
        Long targetProfileId = member.getProfiles().get(0).getProfileId();
        DeleteProfileDto deleteProfileDto = new DeleteProfileDto();
        deleteProfileDto.setProfileId(targetProfileId);

        mockMvc.perform(delete("/api/profile")// 메인 프로필이 한개밖에 없기 때문에 실패
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(deleteProfileDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("프로필은 최소 한개 가지고 있어야 합니다."));
    }

    /**
     * 프로필 삭제 성공 테스트(1. 메인프로필을 삭제할때, 메인 프로필이 아닌걸 삭제할때)
     */
    @Test
//    @Rollback(value = false)
    void deleteProfile_success() throws Exception {
        // 프로필 하 나 더 추 가
        addProfile_success();
        // 프로필 삭제전 수정할 프로필 선택
        Member member = memberRepository.findByLoginId("testMember").orElseThrow(() -> new RuntimeException("MEMBER_NOT_FOUND"));
//        Long targetProfileId = member.getProfiles().get(0).getProfileId(); // 메인 프로필 삭제
        Long targetProfileId = member.getProfiles().get(1).getProfileId(); // 메인 프로필 아닌거 삭제
        DeleteProfileDto deleteProfileDto = new DeleteProfileDto();
        deleteProfileDto.setProfileId(targetProfileId);

        mockMvc.perform(delete("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(deleteProfileDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        assertEquals(1, member.getProfiles().size());
    }


}