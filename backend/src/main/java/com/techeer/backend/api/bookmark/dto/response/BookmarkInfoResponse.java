package com.techeer.backend.api.bookmark.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BookmarkInfoResponse(
    Long id,
    Long jobPostingId,
    String jobPostingTitle,
    String companyName,
    LocalDateTime createdAt
) {}

