package com.mz_test.mz_test.test.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz_test.mz_test.domain.api.member.dto.request.AddMemberDto;
import com.mz_test.mz_test.domain.api.member.dto.request.LoginDto;
import com.mz_test.mz_test.domain.api.member.dto.response.MemberDto;
import com.mz_test.mz_test.domain.api.member.dto.response.MembersDto;
import com.mz_test.mz_test.domain.api.member.entity.Member;
import com.mz_test.mz_test.domain.api.member.repository.MemberRepository;
import com.mz_test.mz_test.domain.api.profile.dto.request.AddProfileDto;
import com.mz_test.mz_test.domain.api.profile.dto.response.ProfileDto;
import com.mz_test.mz_test.domain.api.profile.entity.Profile;
import com.mz_test.mz_test.domain.api.role.dto.request.ChangeRoleDto;
import com.mz_test.mz_test.global.util.BcryptUtil;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;
    private String token; // api 인증용 토큰

    @BeforeEach
    void setup() throws Exception {
        token = adminLogin(); // 테스트 전에 관리자로 로그인후 토큰을 리턴받아 전역 변수에 저장
        addMember();
    }

    /**
     * 권한 변경 성공 테스트
     */
    @Test
    void changeRole_success() throws Exception {


        Member member = memberRepository.findByLoginId("ROLEROLE").get();

        ChangeRoleDto changeRoleDto = new ChangeRoleDto();
        changeRoleDto.setMemberId(member.getMemberId());


        mockMvc.perform(put("/api/role/admin") // 어드민으로 변환
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(changeRoleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));

        assertEquals(member.getRole(), "admin"); // 어드민 확인

        mockMvc.perform(put("/api/role/member") // 멤버로 변환
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(changeRoleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));

        assertEquals(member.getRole(), "member"); // 멤버 확인
    }

//    @Rollback(value = false)
    private String adminLogin() throws Exception {

        String adminId = "admin";
        String adminSalt = BcryptUtil.generateSalt();
        String adminPassword = "adminPassword";
        AddProfileDto addProfileDto = new AddProfileDto();
        addProfileDto.setAddress(null);
        addProfileDto.setNickname("adminNickname");
        addProfileDto.setPhone("010-4420-484");

        AddMemberDto addMemberDto = new AddMemberDto();
        addMemberDto.setLoginId(adminId);
        addMemberDto.setName("adminName");
        addMemberDto.setPassword(BcryptUtil.hashPasswordWithSalt(adminPassword, adminSalt));
        addMemberDto.setAddProfileDto(addProfileDto);
        addMemberDto.setSalt(adminSalt);

        Member member = Member.createWithProfile(addMemberDto);
        member.promoteToAdmin();
        memberRepository.save(member);

        LoginDto loginDto = new LoginDto();
        loginDto.setId(adminId);
        loginDto.setPassword(adminPassword);

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

    public void addMember() throws Exception {

        AddProfileDto addProfileDto = new AddProfileDto();
        addProfileDto.setAddress(null); // 주소는 null가능
        addProfileDto.setNickname("ROLETEST");
        addProfileDto.setPhone("010-4420-2222");

        AddMemberDto addMemberDto = new AddMemberDto();
        addMemberDto.setLoginId("ROLEROLE");
        addMemberDto.setName("LOL");
        addMemberDto.setPassword("ROLEPASSWORD");
        addMemberDto.setAddProfileDto(addProfileDto);


        mockMvc
                .perform(post("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addMemberDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));
    }
}
