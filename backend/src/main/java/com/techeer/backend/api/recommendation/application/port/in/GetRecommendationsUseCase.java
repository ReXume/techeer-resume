package com.techeer.backend.api.recommendation.application.port.in;

import com.techeer.backend.api.recommendation.dto.response.RecommendationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetRecommendationsUseCase {

	Page<RecommendationResponse> getRecommendations(Long userId, Pageable pageable);

}
