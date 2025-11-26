package com.techeer.backend.global.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisConnectionChecker {

	private final RedisConnectionFactory redisConnectionFactory;

	@PostConstruct
	public void checkRedisConnection() {
		try {
			redisConnectionFactory.getConnection().ping();
			log.info("✅ Redis 연결 성공!");
		}
		catch (Exception e) {
			log.error("❌ Redis 연결 실패!", e);
		}
	}

}
