package com.techeer.backend.global.redis;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class RedisService {
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    private final StringRedisTemplate stringRedisTemplate;

    public String refreshTokenGet(String refreshToken) {
        return stringRedisTemplate.opsForValue().get("refreshToken:" + refreshToken);
    }

    public void cacheRefreshToken(String refreshToken) {
        String key = "refreshToken:" + refreshToken;
        log.info("refresh token cache key: {}", key);
        stringRedisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(refreshTokenExpirationPeriod));
    }

    public void deleteCacheRefreshToken(String refreshToken) {
        String key = "refreshToken:" + refreshToken;
        log.info("refresh token cache key: {}", key);

        Boolean hasKey = stringRedisTemplate.hasKey(key);
        if (Boolean.FALSE.equals(hasKey)) {
            log.info("caching 되지 않은 refresh token 입니다.");
            return;
        }
        stringRedisTemplate.delete(key);
    }
}
