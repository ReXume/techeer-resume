package com.techeer.backend.api.recommendation.application.port.out;

import com.techeer.backend.api.recommendation.domain.Recommendation;
import java.util.List;

public interface SaveRecommendationPort {

	Recommendation saveRecommendation(Recommendation recommendation);

	List<Recommendation> saveAllRecommendations(List<Recommendation> recommendations);

	void deleteAllByUserId(Long userId);

}
