package com.techeer.backend.api.recommendation.application.port.in;

public interface MarkRecommendationViewedUseCase {

	void markAsViewed(Long recommendationId);

}
