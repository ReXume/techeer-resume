package com.techeer.backend.api.job.dto.request;

import com.techeer.backend.api.job.domain.EventType;
import jakarta.validation.constraints.NotNull;

public record UserEventRequest(
    @NotNull EventType eventType,
    Long jobPostingId,
    String metadata
) {

}
