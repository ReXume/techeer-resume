package com.techeer.backend.api.job.dto.response;

import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record JobSearchResponse(
	List<JobSearchHit> results,
	Map<String, Map<String, Long>> facets,
	long totalCount,
	int page,
	int size,
	int totalPages
) {

	@Builder
	public record JobSearchHit(
		Long id,
		String title,
		String companyName,
		String position,
		String experienceLevel,
		List<String> requiredSkills,
		String location,
		Long salaryMin,
		Long salaryMax,
		String deadline,
		String status,
		String source,
		long viewCount,
		long applyClickCount,
		String createdAt,
		double score
	) {

	}

}
