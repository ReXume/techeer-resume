package com.techeer.backend.api.job.dto.response;

import lombok.Builder;

@Builder
public record JobPostingInfoResponse(
    Long id,
    Long companyId,
    String companyName,
    String title,
    String contents,
    Integer expYears,
    String status
) {}

