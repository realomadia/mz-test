package com.mz_test.mz_test.domain.api.member.repository;

import com.mz_test.mz_test.domain.api.member.entity.Member;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String testUser);

    void deleteByLoginId(String memberId);

    Page<Member> findByNameContainingIgnoreCase(String searchName, Pageable pageable);
}
