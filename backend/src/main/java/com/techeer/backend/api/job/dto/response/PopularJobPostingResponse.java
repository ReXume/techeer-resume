package com.techeer.backend.api.job.dto.response;

public record PopularJobPostingResponse(
    Long jobPostingId,
    Long viewCount,
    Long clickCount
) {

}
