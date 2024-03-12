package com.mz_test.repository;

import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import com.mz_test.exception.MemberNotFoundException;
import com.mz_test.fixture.MemberFixture;
import com.mz_test.fixture.ProfileFixture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
@Transactional
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setup() throws Exception {
        // Given
        addData();
    }

    @Test
    @DisplayName("존재하는 회원이 주어질시 회원의 프로필정보들을 리턴")
    public void testFindAllByMember() {
        // Given
        Member member = memberRepository.findById(1L).orElseThrow(() -> new MemberNotFoundException(1L));

        // When
        List<Profile> profiles = profileRepository.findAllByMember(member);
        // Then
        assertThat(profiles).isNotNull();
        assertThat(profiles.get(0).getAddress()).isEqualTo("texas");
    }

    @Test
    @DisplayName("올바른 회원이 주어질시 회원을 삭제")
    public void testDeleteByMember() {
        // Given
        Member member = memberRepository.findById(1L).orElseThrow(() -> new MemberNotFoundException(1L));

        // When
        profileRepository.deleteByMember(member);

        // Then
        ObjectUtils.isEmpty(assertThat(profileRepository.findAllByMember(member)));
    }

    @Test
    public void testFindByIsMain() {
        // Given
        String searchKeyword = "name"; // 테스트할 검색 키워드를 설정합니다.
        Pageable pageable = PageRequest.of(0, 3);

        // When
        Page<Profile> page = profileRepository.findByIsMain(searchKeyword, pageable);

        // Then
        assertThat(page).isNotNull();
        assertThat(page.getContent().size()).isEqualTo(3);
        assertThat(page.getContent().get(0).getNickname()).isEqualTo("testNickname");
    }


    public void addData() {
        for (int i = 0; i <= 9; i++) {
            Member member = MemberFixture.idMember("loginId" + i);
            Profile profile = ProfileFixture.profile(member);
            Profile profile1 = ProfileFixture.mainProfile(member);
            memberRepository.save(member);
            profileRepository.save(profile);
            profileRepository.save(profile1);
        }
    }
}