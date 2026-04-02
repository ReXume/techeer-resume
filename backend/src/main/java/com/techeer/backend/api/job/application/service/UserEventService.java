package com.techeer.backend.api.job.application.service;

import com.techeer.backend.api.job.application.port.out.LoadUserEventPort;
import com.techeer.backend.api.job.application.port.out.SaveUserEventPort;
import com.techeer.backend.api.job.domain.EventType;
import com.techeer.backend.api.job.domain.UserEvent;
import com.techeer.backend.api.job.dto.response.PopularJobPostingResponse;
import com.techeer.backend.api.job.dto.response.UserEventResponse;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserEventService {

    private static final Duration IDEMPOTENCY_TTL = Duration.ofSeconds(5);

    private final SaveUserEventPort saveUserEventPort;

    private final LoadUserEventPort loadUserEventPort;

    private final StringRedisTemplate stringRedisTemplate;

    public UserEventResponse recordEvent(Long userId, Long jobPostingId, EventType eventType, String metadata) {
        String idempotencyKey = buildIdempotencyKey(userId, jobPostingId, eventType);

        Boolean isNew = stringRedisTemplate.opsForValue()
            .setIfAbsent(idempotencyKey, "1", IDEMPOTENCY_TTL);

        if (Boolean.FALSE.equals(isNew)) {
            log.debug("Duplicate event detected, skipping: key={}", idempotencyKey);
            return null;
        }

        UserEvent userEvent = UserEvent.builder()
            .userId(userId)
            .jobPostingId(jobPostingId)
            .eventType(eventType)
            .metadata(metadata)
            .build();

        UserEvent saved = saveUserEventPort.saveUserEvent(userEvent);

        incrementRedisCounters(jobPostingId, eventType);

        return UserEventResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<PopularJobPostingResponse> getPopularJobPostings(int limit) {
        List<UserEvent> topEvents = loadUserEventPort.findTopJobPostingsByEventType(EventType.VIEW, limit);

        return topEvents.stream()
            .map(event -> {
                Long jobPostingId = event.getJobPostingId();
                Long viewCount = getRedisCount(buildViewCountKey(jobPostingId));
                Long clickCount = getRedisCount(buildClickCountKey(jobPostingId));
                return new PopularJobPostingResponse(jobPostingId, viewCount, clickCount);
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserEventResponse> getUserEventHistory(Long userId, EventType eventType, int page, int size) {
        Page<UserEvent> events = loadUserEventPort.findByUserIdAndEventType(
            userId, eventType, PageRequest.of(page, size));
        return events.map(UserEventResponse::from);
    }

    private void incrementRedisCounters(Long jobPostingId, EventType eventType) {
        if (jobPostingId == null) {
            return;
        }
        if (EventType.VIEW.equals(eventType)) {
            stringRedisTemplate.opsForValue().increment(buildViewCountKey(jobPostingId));
        } else if (EventType.APPLY_CLICK.equals(eventType)) {
            stringRedisTemplate.opsForValue().increment(buildClickCountKey(jobPostingId));
        }
    }

    private Long getRedisCount(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            return 0L;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String buildIdempotencyKey(Long userId, Long jobPostingId, EventType eventType) {
        return String.format("event:%s:%s:%s", userId, jobPostingId, eventType.name());
    }

    private String buildViewCountKey(Long jobPostingId) {
        return String.format("job:%s:views", jobPostingId);
    }

    private String buildClickCountKey(Long jobPostingId) {
        return String.format("job:%s:clicks", jobPostingId);
    }

}
