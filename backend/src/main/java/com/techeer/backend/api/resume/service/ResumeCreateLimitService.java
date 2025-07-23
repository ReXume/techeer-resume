package com.techeer.backend.api.resume.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeCreateLimitService {
    private final StringRedisTemplate redisTemplate;

    private static final Duration TTL = Duration.ofSeconds(5);

    public boolean isLimited(Long userId) {
        String key = buildKey(userId);
        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            return true; // 요청 제한
        }
        // 키가 없으면 생성
        redisTemplate.opsForValue().set(key, "1", TTL);
        return false;
    }

    private String buildKey(Long userId) {
        return "resume:create:" + userId;
    }
}

