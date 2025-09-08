package com.sillim.recordit.global.lock;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LockManager {

	private final RedisTemplate<String, String> redisTemplate;

	public boolean lock(String key) {
		return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "lock", Duration.ofSeconds(3)));
	}

	public boolean unlock(String key) {
		return redisTemplate.delete(key);
	}
}
