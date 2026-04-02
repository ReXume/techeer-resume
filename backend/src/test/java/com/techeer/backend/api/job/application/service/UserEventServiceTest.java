package com.techeer.backend.api.job.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.techeer.backend.api.job.application.port.out.LoadUserEventPort;
import com.techeer.backend.api.job.application.port.out.SaveUserEventPort;
import com.techeer.backend.api.job.domain.EventType;
import com.techeer.backend.api.job.domain.UserEvent;
import com.techeer.backend.api.job.dto.response.PopularJobPostingResponse;
import com.techeer.backend.api.job.dto.response.UserEventResponse;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class UserEventServiceTest {

    @Mock
    private SaveUserEventPort saveUserEventPort;

    @Mock
    private LoadUserEventPort loadUserEventPort;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private UserEventService userEventService;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        userEventService = new UserEventService(saveUserEventPort, loadUserEventPort, stringRedisTemplate);
    }

    private UserEvent buildUserEvent(Long id, Long userId, Long jobPostingId, EventType eventType) {
        UserEvent event = UserEvent.builder()
            .userId(userId)
            .jobPostingId(jobPostingId)
            .eventType(eventType)
            .metadata(null)
            .build();
        return event;
    }

    @Nested
    @DisplayName("recordEvent() — 이벤트 기록")
    class RecordEvent {

        @Test
        @DisplayName("신규 이벤트는 저장되어야 한다")
        void saves_new_event() {
            // Given
            UserEvent saved = buildUserEvent(1L, 1L, 10L, EventType.VIEW);
            given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(true);
            given(saveUserEventPort.saveUserEvent(any(UserEvent.class))).willReturn(saved);

            // When
            UserEventResponse response = userEventService.recordEvent(1L, 10L, EventType.VIEW, null);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.eventType()).isEqualTo(EventType.VIEW);
            verify(saveUserEventPort, times(1)).saveUserEvent(any(UserEvent.class));
        }

        @Test
        @DisplayName("중복 이벤트는 저장하지 않아야 한다 (Redis 멱등성)")
        void skips_duplicate_event() {
            // Given
            given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(false);

            // When
            UserEventResponse response = userEventService.recordEvent(1L, 10L, EventType.VIEW, null);

            // Then
            assertThat(response).isNull();
            verify(saveUserEventPort, never()).saveUserEvent(any());
        }

        @Test
        @DisplayName("VIEW 이벤트 시 Redis 조회수 카운터가 증가해야 한다")
        void increments_view_counter_on_view_event() {
            // Given
            UserEvent saved = buildUserEvent(1L, 1L, 10L, EventType.VIEW);
            given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(true);
            given(saveUserEventPort.saveUserEvent(any(UserEvent.class))).willReturn(saved);

            // When
            userEventService.recordEvent(1L, 10L, EventType.VIEW, null);

            // Then
            verify(valueOperations, times(1)).increment(eq("job:10:views"));
        }

        @Test
        @DisplayName("APPLY_CLICK 이벤트 시 Redis 클릭 카운터가 증가해야 한다")
        void increments_click_counter_on_apply_click_event() {
            // Given
            UserEvent saved = buildUserEvent(1L, 1L, 10L, EventType.APPLY_CLICK);
            given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(true);
            given(saveUserEventPort.saveUserEvent(any(UserEvent.class))).willReturn(saved);

            // When
            userEventService.recordEvent(1L, 10L, EventType.APPLY_CLICK, null);

            // Then
            verify(valueOperations, times(1)).increment(eq("job:10:clicks"));
        }

        @Test
        @DisplayName("SEARCH 이벤트는 Redis 카운터를 증가시키지 않아야 한다")
        void does_not_increment_counter_for_search_event() {
            // Given
            UserEvent saved = buildUserEvent(1L, 1L, null, EventType.SEARCH);
            given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(true);
            given(saveUserEventPort.saveUserEvent(any(UserEvent.class))).willReturn(saved);

            // When
            userEventService.recordEvent(1L, null, EventType.SEARCH, "{\"query\":\"java developer\"}");

            // Then
            verify(valueOperations, never()).increment(anyString());
        }
    }

    @Nested
    @DisplayName("getPopularJobPostings() — 인기 채용공고 조회")
    class GetPopularJobPostings {

        @Test
        @DisplayName("인기 채용공고 목록을 Redis 카운터와 함께 반환해야 한다")
        void returns_popular_job_postings_with_counters() {
            // Given
            UserEvent event1 = buildUserEvent(1L, 1L, 10L, EventType.VIEW);
            UserEvent event2 = buildUserEvent(2L, 2L, 20L, EventType.VIEW);
            given(loadUserEventPort.findTopJobPostingsByEventType(eq(EventType.VIEW), eq(5))).willReturn(
                List.of(event1, event2));
            given(valueOperations.get("job:10:views")).willReturn("42");
            given(valueOperations.get("job:10:clicks")).willReturn("5");
            given(valueOperations.get("job:20:views")).willReturn("30");
            given(valueOperations.get("job:20:clicks")).willReturn(null);

            // When
            List<PopularJobPostingResponse> result = userEventService.getPopularJobPostings(5);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).jobPostingId()).isEqualTo(10L);
            assertThat(result.get(0).viewCount()).isEqualTo(42L);
            assertThat(result.get(0).clickCount()).isEqualTo(5L);
            assertThat(result.get(1).jobPostingId()).isEqualTo(20L);
            assertThat(result.get(1).viewCount()).isEqualTo(30L);
            assertThat(result.get(1).clickCount()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("getUserEventHistory() — 이벤트 히스토리 조회")
    class GetUserEventHistory {

        @Test
        @DisplayName("사용자의 이벤트 히스토리를 페이지로 반환해야 한다")
        void returns_paginated_event_history() {
            // Given
            UserEvent event = buildUserEvent(1L, 1L, 10L, EventType.VIEW);
            Page<UserEvent> page = new PageImpl<>(List.of(event), PageRequest.of(0, 20), 1);
            given(loadUserEventPort.findByUserIdAndEventType(eq(1L), eq(EventType.VIEW), any())).willReturn(page);

            // When
            Page<UserEventResponse> result = userEventService.getUserEventHistory(1L, EventType.VIEW, 0, 20);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).eventType()).isEqualTo(EventType.VIEW);
        }
    }

}
