package com.careerpirates.resumate.auth.infrastructure;

import java.time.Duration;
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
		stringRedisTemplate.opsForValue().set(PREFIX + key, value, duration);
	}

	public Optional<String> findByKey(String key) {
		return Optional.ofNullable(stringRedisTemplate.opsForValue().get(PREFIX + key));
	}

	public Boolean deleteByKey(String key) {
		return stringRedisTemplate.delete(PREFIX + key);
	}
}
