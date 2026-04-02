package com.techeer.backend.api.job.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record SearchCriteria(
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

	public static SearchCriteria from(JobSearchRequest request) {
		return SearchCriteria.builder()
			.query(request.query())
			.position(request.position())
			.experienceLevel(request.experienceLevel())
			.skills(request.skills())
			.location(request.location())
			.source(request.source())
			.salaryMin(request.salaryMin())
			.salaryMax(request.salaryMax())
			.deadlineType(request.deadlineType())
			.page(request.page())
			.size(request.size())
			.build();
	}

}
