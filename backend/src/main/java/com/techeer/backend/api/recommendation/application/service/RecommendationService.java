package com.techeer.backend.api.recommendation.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techeer.backend.api.job.adapter.out.persistence.JobPostingJpaRepository;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.recommendation.application.port.in.GetRecommendationsUseCase;
import com.techeer.backend.api.recommendation.application.port.in.MarkRecommendationViewedUseCase;
import com.techeer.backend.api.recommendation.application.port.out.LoadRecommendationPort;
import com.techeer.backend.api.recommendation.application.port.out.SaveRecommendationPort;
import com.techeer.backend.api.recommendation.domain.Recommendation;
import com.techeer.backend.api.recommendation.dto.response.RecommendationResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService implements GetRecommendationsUseCase, MarkRecommendationViewedUseCase {

	private static final String REDIS_KEY_PREFIX = "recommend:";

	private final LoadRecommendationPort loadRecommendationPort;

	private final SaveRecommendationPort saveRecommendationPort;

	private final JobPostingJpaRepository jobPostingJpaRepository;

	private final StringRedisTemplate stringRedisTemplate;

	private final ObjectMapper objectMapper;

	/**
	 * Get recommendations for a user. Tries Redis cache first; falls back to DB.
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<RecommendationResponse> getRecommendations(Long userId, Pageable pageable) {
		// Try Redis cache first
		String cacheKey = REDIS_KEY_PREFIX + userId;
		String cached = stringRedisTemplate.opsForValue().get(cacheKey);
		if (cached != null) {
			try {
				List<Map<String, Object>> cacheData = objectMapper.readValue(
					cached, new TypeReference<>() {
					});
				int start = (int) pageable.getOffset();
				int end = Math.min(start + pageable.getPageSize(), cacheData.size());
				if (start >= cacheData.size()) {
					return Page.empty(pageable);
				}
				List<RecommendationResponse> pageContent = cacheData.subList(start, end).stream()
					.map(entry -> RecommendationResponse.builder()
						.jobPostingId(((Number) entry.get("jobPostingId")).longValue())
						.title((String) entry.get("title"))
						.matchScore(((Number) entry.get("matchScore")).doubleValue())
						.build())
					.toList();
				return new PageImpl<>(pageContent, pageable, cacheData.size());
			}
			catch (Exception e) {
				log.warn("Failed to deserialize cached recommendations for user {}: {}", userId, e.getMessage());
			}
		}

		// DB fallback
		return loadRecommendationPort.findAllByUserId(userId, pageable)
			.map(this::toResponse);
	}

	/**
	 * Mark a recommendation as viewed.
	 */
	@Override
	@Transactional
	public void markAsViewed(Long recommendationId) {
		Recommendation recommendation = loadRecommendationPort.findById(recommendationId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
		recommendation.markAsViewed();
		saveRecommendationPort.saveRecommendation(recommendation);
	}

	private RecommendationResponse toResponse(Recommendation rec) {
		String title = null;
		String companyName = null;
		JobPosting jp = jobPostingJpaRepository.findById(rec.getJobPostingId()).orElse(null);
		if (jp != null) {
			title = jp.getTitle();
			if (jp.getCompany() != null) {
				companyName = jp.getCompany().getName();
			}
		}

		Map<String, Double> matchReasons = null;
		if (rec.getMatchReasons() != null) {
			try {
				matchReasons = objectMapper.readValue(rec.getMatchReasons(), new TypeReference<>() {
				});
			}
			catch (Exception e) {
				log.warn("Failed to parse matchReasons for recommendation {}", rec.getId());
			}
		}

		return RecommendationResponse.builder()
			.id(rec.getId())
			.jobPostingId(rec.getJobPostingId())
			.title(title)
			.companyName(companyName)
			.matchScore(rec.getMatchScore() != null ? rec.getMatchScore().getValue() : null)
			.matchReasons(matchReasons)
			.isViewed(rec.getIsViewed())
			.build();
	}

}
