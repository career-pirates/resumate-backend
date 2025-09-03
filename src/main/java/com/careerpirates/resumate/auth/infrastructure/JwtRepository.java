package com.careerpirates.resumate.auth.infrastructure;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JwtRepository {

	private final StringRedisTemplate stringRedisTemplate;
	private final String PREFIX = "refreshToken:";

	public void saveWithTTL(String key, String value, Duration duration) {
		stringRedisTemplate.opsForValue().set(PREFIX + key, sha256(value), duration);
	}

	public Optional<String> findByKey(String key) {
		return Optional.ofNullable(stringRedisTemplate.opsForValue().get(PREFIX + key));
	}

	public Boolean deleteByKey(String key) {
		return stringRedisTemplate.delete(PREFIX + key);
	}

	public boolean matches(String key, String rawToken) {
		String stored = stringRedisTemplate.opsForValue().get(PREFIX + key);

		return stored != null && stored.equals(sha256(rawToken));
	}

	private static String sha256(String value) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] digest = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));

			return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 not available", e);
		}
	}
}
