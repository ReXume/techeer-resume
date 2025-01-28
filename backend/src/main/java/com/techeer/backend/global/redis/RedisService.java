package com.techeer.backend.global.redis;


import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class RedisService {
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    private final RedisTemplate<String, String> redisTemplate;

    public String refreshTokenGet(String refreshToken) {
        return redisTemplate.opsForValue().get("refreshToken:"+refreshToken);
    }

    public void cacheRefreshToken(String refreshToken) {
        String key = "refreshToken:" + refreshToken;
        log.info("refresh token cache key: {}", key);
        // 리프레시 토큰을 Redis에 저장 (예: 7일 만료)
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(refreshTokenExpirationPeriod));
    }

    public void deleteCacheRefreshToken(String refreshToken) {
        String key = "refreshToken:" + refreshToken;
        log.info("refresh token cache key: {}", key);
        redisTemplate.delete(key);
    }
}
