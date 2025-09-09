package com.careerpirates.resumate.member.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.member.domain.enums.OAuthProvider;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByProviderAndEmail(OAuthProvider provider, String email);

	Optional<Member> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

	/**
	 * 특정 회원의 연속 일수만 조회
	 * @param memberId 회원 ID
	 * @return 연속 일수 (회원이 없으면 Optional.empty())
	 */
	@Query("SELECT m.continuousDays FROM Member m WHERE m.id = :memberId")
	Optional<Long> findContinuousDaysById(@Param("memberId") Long memberId);
}
