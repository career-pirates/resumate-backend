package com.careerpirates.resumate.member.domain.entity;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
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
}
