package com.mz_test.repository;

import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import com.mz_test.exception.MemberNotFoundException;
import com.mz_test.fixture.MemberFixture;
import com.mz_test.fixture.ProfileFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
@Transactional
class MemberRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private MemberRepository memberRepository;


    @Test
    void findByLoginId() {

        Member member = MemberFixture.idMember();
        memberRepository.save(member);

        Member findMember = memberRepository.findByLoginId(member.getLoginId()).orElseThrow(() -> new MemberNotFoundException(3L));

        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    void existsByLoginId() {
        Member member = MemberFixture.idMember();
        memberRepository.save(member);

        boolean isExist = memberRepository.existsByLoginId(member.getLoginId());
        boolean isExist2 = memberRepository.existsByLoginId("nope");
        assertThat(isExist).isTrue();
        assertThat(isExist2).isFalse();
    }

}