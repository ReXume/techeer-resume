package com.techeer.backend.api.job.domain;

import com.techeer.backend.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_event_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "job_posting_id")
    private Long jobPostingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventType eventType;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Builder
    public UserEvent(Long userId, Long jobPostingId, EventType eventType, String metadata) {
        this.userId = userId;
        this.jobPostingId = jobPostingId;
        this.eventType = eventType;
        this.metadata = metadata;
    }

}
