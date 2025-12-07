package com.techeer.backend.api.career.dto.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UserCareerInfoResponse(
    Long id,
    Long userId,
    String companyName,
    String jobTitle,
    Boolean isCurrent,
    LocalDate startDate,
    LocalDate endDate
) {}

