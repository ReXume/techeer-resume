package com.techeer.backend.api.job.dto.response;

import com.techeer.backend.api.job.domain.EventType;
import com.techeer.backend.api.job.domain.UserEvent;
import java.time.LocalDateTime;

public record UserEventResponse(
    Long id,
    Long userId,
    Long jobPostingId,
    EventType eventType,
    String metadata,
    LocalDateTime createdAt
) {

    public static UserEventResponse from(UserEvent userEvent) {
        return new UserEventResponse(
            userEvent.getId(),
            userEvent.getUserId(),
            userEvent.getJobPostingId(),
            userEvent.getEventType(),
            userEvent.getMetadata(),
            userEvent.getCreatedAt()
        );
    }

}
