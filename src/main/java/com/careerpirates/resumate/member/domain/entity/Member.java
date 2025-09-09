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

	@Column(name = "continuous_days")
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
}
