package com.mz_test.mz_test.domain.api.profile.repository;

import com.mz_test.mz_test.domain.api.member.entity.Member;
import com.mz_test.mz_test.domain.api.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByProfileIdAndMember(Long profileId, Member member);

}
