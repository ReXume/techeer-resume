package com.techeer.backend.api.application.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ApplicationInfoResponse(
    Long id,
    Long jobPostingId,
    String jobPostingTitle,
    String companyName,
    String status,
    LocalDateTime appliedAt
) {}

