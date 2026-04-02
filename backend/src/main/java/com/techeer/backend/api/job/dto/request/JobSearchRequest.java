package com.techeer.backend.api.job.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record JobSearchRequest(
	String query,
	String position,
	String experienceLevel,
	List<String> skills,
	String location,
	String source,
	Long salaryMin,
	Long salaryMax,
	String deadlineType,
	int page,
	int size
) {

	public JobSearchRequest {
		if (page < 0) {
			page = 0;
		}
		if (size <= 0) {
			size = 10;
		}
	}

}
