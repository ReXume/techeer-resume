package com.techeer.backend.api.job.domain.event;

import com.techeer.backend.api.job.domain.SourceType;
import java.time.LocalDateTime;

public record JobPostingCreatedEvent(
	Long jobPostingId,
	String title,
	SourceType source,
	LocalDateTime createdAt
) {

	public static JobPostingCreatedEvent of(Long jobPostingId, String title, SourceType source) {
		return new JobPostingCreatedEvent(jobPostingId, title, source, LocalDateTime.now());
	}

}
