package com.careerpirates.resumate.member.domain.entity;

import java.time.LocalDate;
import java.time.Period;

import com.careerpirates.resumate.global.domain.BaseEntity;
import com.careerpirates.resumate.member.domain.enums.OAuthProvider;
import com.careerpirates.resumate.member.domain.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "member",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_member_provider_user", columnNames = {"provider", "provider_user_id"})
		// @UniqueConstraint(name = "uk_member_email", columnNames = {"email"})
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "provider", nullable = false)
	private OAuthProvider provider;

	@Column(name = "provider_user_id", nullable = false)
	private String providerUserId;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@Column(name = "continuous_days", nullable = false)
	private Long continuousDays;

	@Column(name = "last_review_date")
	private LocalDate lastReviewDate;

	@Builder
	public Member(String providerUserId, OAuthProvider provider, String email, String nickname, Role role) {
		this.providerUserId = providerUserId;
		this.provider = provider;
		this.email = email;
		this.nickname = nickname;
		this.role = role;
		this.continuousDays = 0L;
	}

	public void updateNickName(String nickname) {
		this.nickname = nickname;
	}

	public void updateLastReviewDate() {
		LocalDate today = LocalDate.now();

		// 첫 번째 리뷰이거나 이미 오늘 업데이트된 경우
		if (lastReviewDate == null) {
			this.lastReviewDate = today;
			this.continuousDays = 1L;
			return;
		}

		if (lastReviewDate.isEqual(today)) {
			return;
		}

		if (isConsecutiveDay(lastReviewDate, today)) {
			this.continuousDays++;
		} else {
			// 연속이 끊어진 경우 1부터 다시 시작
			this.continuousDays = 1L;
		}

		this.lastReviewDate = today;
	}

	/**
	 * 두 날짜가 연속된 날짜인지 확인
	 * @param previousDate 이전 날짜
	 * @param currentDate 현재 날짜
	 * @return 연속된 날짜인지 여부
	 */
	private boolean isConsecutiveDay(LocalDate previousDate, LocalDate currentDate) {
		return Period.between(previousDate, currentDate).getDays() == 1;
	}

	/**
	 * 연속 일수 초기화 (필요시 외부에서 호출)
	 */
	public void resetContinuousDays() {
		this.continuousDays = 0L;
		this.lastReviewDate = null;
	}
}
