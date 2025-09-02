package com.careerpirates.resumate.member.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.member.domain.enums.OAuthProvider;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByProviderAndEmail(OAuthProvider provider, String email);
}
