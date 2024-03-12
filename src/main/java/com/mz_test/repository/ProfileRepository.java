package com.mz_test.repository;

import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import com.mz_test.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findAllByMember(Member member);

    void deleteByMember(Member member);

    @Query("SELECT p FROM Profile p WHERE p.isMain = 1 AND p.member.name LIKE %:searchKeyword%")
    Page<Profile> findByIsMain(String searchKeyword, Pageable pageable);
}
