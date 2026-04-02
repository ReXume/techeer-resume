package com.techeer.backend.api.recommendation.dto.response;

import java.util.Map;
import lombok.Builder;

@Builder
public record RecommendationResponse(
	Long id,
	Long jobPostingId,
	String title,
	String companyName,
	Double matchScore,
	Map<String, Double> matchReasons,
	Boolean isViewed
) {

}
