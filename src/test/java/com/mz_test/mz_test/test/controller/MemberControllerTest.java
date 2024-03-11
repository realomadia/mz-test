package com.mz_test.mz_test.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.mz_test.mz_test.global.util.BcryptUtil;
import com.mz_test.mz_test.global.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;
    private String token; // api 인증용 토큰


    /**
     * 회원가입 성공 테스트
     */
    @Test
//    @Rollback(value = false)
    void addMember_success() throws Exception {
        // Given


        AddProfileDto addProfileDto = new AddProfileDto();
        addProfileDto.setAddress(null); // 주소는 null가능
        addProfileDto.setNickname("TEST NICKNAME");
        addProfileDto.setPhone("010-4420-4840");

        AddMemberDto addMemberDto = new AddMemberDto();
        addMemberDto.setLoginId("testMember");
        addMemberDto.setName("Test Member");
        addMemberDto.setPassword("password");
        addMemberDto.setAddProfileDto(addProfileDto);

        // When & then
        mockMvc
                .perform(post("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addMemberDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));

        Member savedMember = memberRepository.findByLoginId("testMember").orElseThrow(() -> new RuntimeException("MEMBER NOT FOUND"));
        assertNotNull(savedMember);
        assertEquals("testMember", savedMember.getLoginId());
        assertEquals("Test Member", savedMember.getName());
        assertNotEquals("password", savedMember.getPassword()); // 패스워드는 암호화되서 다름

        List<Profile> profiles = savedMember.getProfiles();
        assertNotNull(profiles);
        assertEquals(1, profiles.size());

        Profile savedProfile = profiles.get(0);
        assertEquals("TEST NICKNAME", savedProfile.getNickname());
        assertEquals("010-4420-4840", savedProfile.getPhone());
        assertEquals(null, savedProfile.getAddress());

    }

    /**
     * 회원가입 실패 테스트(아이디 중복 테스트)
     */
    @Test
    void addMember_fail() throws Exception {
        // Given
        addMember_success();

        // Given - 동일한 loginId를 가진 두 번째 사용자
        AddProfileDto addProfileDto = new AddProfileDto();
        addProfileDto.setAddress(null); // 주소는 null가능
        addProfileDto.setNickname("TEST NICKNAME");
        addProfileDto.setPhone("010-5466-1111");

        AddMemberDto addMemberDto = new AddMemberDto();
        addMemberDto.setLoginId("testMember"); // testMember이 중복이되어 CustomException 발생
        addMemberDto.setName("Test Member");
        addMemberDto.setPassword("password");
        addMemberDto.setAddProfileDto(addProfileDto);

        // When & Then
        mockMvc
                .perform(post("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addMemberDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("이미 가입된 아이디 입니다. 다른 아이디를 입력하여 주세요."));
    }

    /**
     * 로그인 성공 테스트
     */
    @Test
    void login_success() throws Exception {
        // Given
        addMember_success();

        LoginDto loginDto = new LoginDto();
        loginDto.setId("testMember");
        loginDto.setPassword("password");
        // When & Then
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
    }

    /**
     * 로그인 실패 테스트
     */
    @Test
    void login_fail() throws Exception {
        // Given
        addMember_success();

        LoginDto loginDto = new LoginDto();
        loginDto.setId("testMember");
        loginDto.setPassword("password1"); // 틀린 패스워드 전달해 CustomException 발생
        // When & Then
        mockMvc
                .perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요."));
    }

    /**
     * 회원탈퇴 성공 테스트
     */
    @Test
//    @Rollback(value = false)
    void deleteMember_success() throws Exception {
        // Given
        token = loginAndGetToken();
        // When & Then
        mockMvc
                .perform(delete("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        Member deleteMember = memberRepository.findByLoginId("imdelete").orElse(null);
        assertNull(deleteMember);
    }

    /**
     * 회원 전체 조회 성공 테스트
     */
    @Test
    void getAllMembers_success() throws Exception {

        // 관리자 로그인
        token = adminLogin();

        addMembers(50);

        int page = 0;
        int size = 3;
        String searchName = "1";

        MvcResult mvcResult = mockMvc.perform(get("/api/member/list")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("searchName", searchName)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // 반환된 JSON 문자열을 List<MemberDto>로 변환
        String content = mvcResult.getResponse().getContentAsString();
        List<MembersDto> membersDtoList = convertJsonToMemberDtoList(content);
        assertEquals(size, membersDtoList.size());

        for(MembersDto dto : membersDtoList){
            log.info("ID : {}", dto.getMemberId());
            log.info("NICKNAME : {}", dto.getProfileDto().getNickname());
            log.info("NAME : {}", dto.getName());
        }

    }

    /**
     * 회원 상세 조회 성공 테스트
     */
    @Test
    void getMember_success() throws Exception {

        // 관리자 로그인
        token = adminLogin();

        addMembers(10);
        int index = 4; // 회원 선택

        // 선택 유저 프로필 5개 등록
        List<Member> members = memberRepository.findAll();
        Member member = members.get(index);
        for (int i = 0; i < 5; i++) {
            AddProfileDto addProfileDto = new AddProfileDto();
            addProfileDto.setAddress(null);
            addProfileDto.setNickname("hi" + i);
            addProfileDto.setPhone("010-2345-6789");
            members.get(index).addProfile(addProfileDto);
            memberRepository.save(members.get(index));
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/member")
                        .param("memberId", String.valueOf(members.get(index).getMemberId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        // 반환된 JSON 문자열을 List<MemberDto>로 변환
        String content = mvcResult.getResponse().getContentAsString();
        MemberDto memberDto = parseJsonToMemberDto(content);
        assertEquals(member.getProfiles().size(), memberDto.getProfileList().size()); // 기본으로 들어가하는 프로필 1개 + 임의로 넣은 프로필 5개
        assertEquals(member.getMemberId(), memberDto.getMemberId());
        assertEquals(member.getProfiles().get(2).getNickname(), memberDto.getProfileList().get(2).getNickname());

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
        addMemberDto.setLoginId("imdelete");
        addMemberDto.setName("Test Member");
        addMemberDto.setPassword("password");
        addMemberDto.setAddProfileDto(addProfileDto);

        // When & then
        mockMvc
                .perform(post("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addMemberDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));

        LoginDto loginDto = new LoginDto();
        loginDto.setId("imdelete");
        loginDto.setPassword("password");
        // When & Then
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

    private void addMembers(int count) {
        for (int i = 0; i < count; i++) {
            AddProfileDto addProfileDto = new AddProfileDto();
            addProfileDto.setAddress(null);
            addProfileDto.setNickname("hello" + i);
            addProfileDto.setPhone("010-4420-484" + i);

            AddMemberDto addMemberDto = new AddMemberDto();
            addMemberDto.setLoginId("ttmember" + i);
            addMemberDto.setName("poomadia" + i);
            addMemberDto.setPassword("poopsword" + i);
            addMemberDto.setAddProfileDto(addProfileDto);
            addMemberDto.setSalt("salt");

            Member member = Member.createWithProfile(addMemberDto);
            memberRepository.save(member);
        }
    }

    private List<MembersDto> convertJsonToMemberDtoList(String jsonString) throws Exception {
        List<MembersDto> membersDtoList = new ArrayList<>();

        JsonNode jsonNode = objectMapper.readTree(jsonString);
        JsonNode contentNode = jsonNode.path("data").path("content");

        for (JsonNode memberNode : contentNode) {
            MembersDto membersDto = convertJsonToMemberDto(memberNode);
            membersDtoList.add(membersDto);
        }

        return membersDtoList;
    }

    private MembersDto convertJsonToMemberDto(JsonNode memberNode) {
        MembersDto membersDto = new MembersDto();

        membersDto.setMemberId(memberNode.path("memberId").asLong());
        membersDto.setLoginId(memberNode.path("loginId").asText());
        membersDto.setName(memberNode.path("name").asText());
        membersDto.setRole(memberNode.path("role").asText());

        ProfileDto profileDto = convertJsonToProfileDto(memberNode.path("profileDto"));
        membersDto.setProfileDto(profileDto);

        return membersDto;
    }

    private ProfileDto convertJsonToProfileDto(JsonNode profileNode) {
        ProfileDto profileDto = new ProfileDto();

        profileDto.setProfileId(profileNode.path("profileId").asLong());
        profileDto.setNickname(profileNode.path("nickname").asText());
        profileDto.setPhone(profileNode.path("phone").asText());
        profileDto.setAddress(profileNode.path("address").asText());

        return profileDto;
    }

    private MemberDto parseJsonToMemberDto(String jsonString) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        JsonNode dataNode = jsonNode.path("data");

        MemberDto memberDto = new MemberDto();
        memberDto.setMemberId(dataNode.path("memberId").asLong());
        memberDto.setLoginId(dataNode.path("loginId").asText());
        memberDto.setName(dataNode.path("name").asText());
        memberDto.setRole(dataNode.path("role").asText());

        // 프로필 정보 파싱
        JsonNode profileListNode = dataNode.path("profileList");
        memberDto.setProfileList(parseJsonToProfileDtoList(profileListNode));

        return memberDto;
    }

    private List<ProfileDto> parseJsonToProfileDtoList(JsonNode profileListNode) {
        List<ProfileDto> profileList = new ArrayList<>();

        for (JsonNode profileNode : profileListNode) {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setProfileId(profileNode.path("profileId").asLong());
            profileDto.setNickname(profileNode.path("nickname").asText());
            profileDto.setPhone(profileNode.path("phone").asText());
            profileDto.setAddress(profileNode.path("address").asText());
            profileList.add(profileDto);
        }

        return profileList;
    }

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
        // When & Then
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

}
