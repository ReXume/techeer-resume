package com.techeer.backend.api.recommendation.adapter.out.persistence;

import com.techeer.backend.api.recommendation.application.port.out.LoadRecommendationPort;
import com.techeer.backend.api.recommendation.application.port.out.SaveRecommendationPort;
import com.techeer.backend.api.recommendation.domain.Recommendation;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RecommendationPersistenceAdapter implements SaveRecommendationPort, LoadRecommendationPort {

	private final RecommendationJpaRepository recommendationJpaRepository;

	@Override
	public Recommendation saveRecommendation(Recommendation recommendation) {
		return recommendationJpaRepository.save(recommendation);
	}

	@Override
	public List<Recommendation> saveAllRecommendations(List<Recommendation> recommendations) {
		return recommendationJpaRepository.saveAll(recommendations);
	}

	@Override
	@Transactional
	public void deleteAllByUserId(Long userId) {
		recommendationJpaRepository.softDeleteAllByUserId(userId);
	}

	@Override
	public Optional<Recommendation> findById(Long id) {
		return recommendationJpaRepository.findById(id);
	}

	@Override
	public Page<Recommendation> findAllByUserId(Long userId, Pageable pageable) {
		return recommendationJpaRepository.findAllByUserIdAndNotDeleted(userId, pageable);
	}

	@Override
	public List<Recommendation> findTopByUserId(Long userId, int limit) {
		return recommendationJpaRepository.findTopByUserId(userId, PageRequest.of(0, limit));
	}

}
