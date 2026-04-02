package com.techeer.backend.api.job.domain.event;

import java.time.LocalDateTime;

public record JobPostingExpiredEvent(
	Long jobPostingId,
	LocalDateTime expiredAt
) {

	public static JobPostingExpiredEvent of(Long jobPostingId) {
		return new JobPostingExpiredEvent(jobPostingId, LocalDateTime.now());
	}

}
