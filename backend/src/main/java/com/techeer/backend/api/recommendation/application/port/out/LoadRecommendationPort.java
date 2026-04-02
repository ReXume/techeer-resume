package com.techeer.backend.api.recommendation.application.port.out;

import com.techeer.backend.api.recommendation.domain.Recommendation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadRecommendationPort {

	Optional<Recommendation> findById(Long id);

	Page<Recommendation> findAllByUserId(Long userId, Pageable pageable);

	List<Recommendation> findTopByUserId(Long userId, int limit);

}
