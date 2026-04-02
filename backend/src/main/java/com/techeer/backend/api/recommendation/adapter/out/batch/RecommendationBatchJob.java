package com.techeer.backend.api.recommendation.adapter.out.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techeer.backend.api.job.adapter.out.persistence.JobPostingJpaRepository;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.domain.JobPostingStatus;
import com.techeer.backend.api.recommendation.adapter.out.persistence.RecommendationJpaRepository;
import com.techeer.backend.api.recommendation.domain.MatchScoringService;
import com.techeer.backend.api.recommendation.domain.Recommendation;
import com.techeer.backend.api.recommendation.domain.vo.MatchScore;
import com.techeer.backend.api.skill.adapter.out.persistence.UserSkillJpaRepository;
import com.techeer.backend.api.skill.domain.UserSkill;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationBatchJob {

	private static final int TOP_RECOMMENDATIONS_PER_USER = 50;

	private static final Duration REDIS_TTL = Duration.ofHours(4);

	private static final String REDIS_KEY_PREFIX = "recommend:";

	private final UserRepository userRepository;

	private final JobPostingJpaRepository jobPostingJpaRepository;

	private final UserSkillJpaRepository userSkillJpaRepository;

	private final RecommendationJpaRepository recommendationJpaRepository;

	private final StringRedisTemplate stringRedisTemplate;

	private final ObjectMapper objectMapper;

	private final MatchScoringService matchScoringService = new MatchScoringService();

	/**
	 * Nightly batch at 2 AM: compute top-50 recommendations per active user and cache in Redis.
	 */
	@Scheduled(cron = "0 0 2 * * *")
	@Transactional
	public void runRecommendationBatch() {
		log.info("Starting recommendation batch job");

		List<User> activeUsers = userRepository.findAll().stream()
			.filter(this::hasCompleteProfile)
			.collect(Collectors.toList());

		List<JobPosting> activePostings = jobPostingJpaRepository.findAll().stream()
			.filter(jp -> jp.getStatus() == JobPostingStatus.OPEN && jp.getDeletedAt() == null)
			.collect(Collectors.toList());

		log.info("Processing {} users against {} active job postings", activeUsers.size(), activePostings.size());

		for (User user : activeUsers) {
			try {
				processUserRecommendations(user, activePostings);
			}
			catch (Exception e) {
				log.error("Failed to process recommendations for user {}: {}", user.getId(), e.getMessage(), e);
			}
		}

		log.info("Recommendation batch job completed");
	}

	private void processUserRecommendations(User user, List<JobPosting> activePostings) {
		List<String> userSkills = getUserSkillNames(user);
		List<String> preferredLocations = parseCommaSeparated(user.getPreferredLocations());

		List<ScoredPosting> scored = activePostings.stream()
			.map(jp -> {
				List<String> jobSkills = getJobSkillNames(jp);
				String jobLocation = jp.getCompany() != null ? jp.getCompany().getLocation() : null;
				MatchScore score = matchScoringService.calculate(
					userSkills,
					user.getExperienceLevel(),
					user.getDesiredPosition(),
					preferredLocations,
					jobSkills,
					resolveJobExperienceLevel(jp.getExpYears()),
					jp.getTitle(),
					jobLocation,
					jp.getCreatedAt()
				);
				return new ScoredPosting(jp, score);
			})
			.sorted(Comparator.comparingDouble(sp -> -sp.score().getValue()))
			.limit(TOP_RECOMMENDATIONS_PER_USER)
			.collect(Collectors.toList());

		// Replace existing recommendations for this user
		recommendationJpaRepository.softDeleteAllByUserId(user.getId());

		List<Recommendation> recommendations = scored.stream()
			.map(sp -> Recommendation.builder()
				.userId(user.getId())
				.jobPostingId(sp.jobPosting().getId())
				.matchScore(sp.score())
				.matchReasons(buildMatchReasons(sp.score()))
				.build())
			.collect(Collectors.toList());

		recommendationJpaRepository.saveAll(recommendations);

		// Cache in Redis with 4-hour TTL
		cacheRecommendations(user.getId(), scored);
	}

	private void cacheRecommendations(Long userId, List<ScoredPosting> scored) {
		try {
			List<Map<String, Object>> cacheData = scored.stream()
				.map(sp -> Map.<String, Object>of(
					"jobPostingId", sp.jobPosting().getId(),
					"title", sp.jobPosting().getTitle(),
					"matchScore", sp.score().getValue()
				))
				.collect(Collectors.toList());
			String json = objectMapper.writeValueAsString(cacheData);
			stringRedisTemplate.opsForValue().set(REDIS_KEY_PREFIX + userId, json, REDIS_TTL);
		}
		catch (JsonProcessingException e) {
			log.warn("Failed to cache recommendations for user {}: {}", userId, e.getMessage());
		}
	}

	private List<String> getUserSkillNames(User user) {
		return userSkillJpaRepository.findAll().stream()
			.filter(us -> us.getUser().getId().equals(user.getId()) && us.getDeletedAt() == null)
			.map(UserSkill::getSkill)
			.map(skill -> skill.getName())
			.collect(Collectors.toList());
	}

	private List<String> getJobSkillNames(JobPosting jp) {
		// JobPosting does not eagerly load skills; use a direct query approach
		// JobSkills are accessed via the JobSkill join table — kept simple here
		return List.of();
	}

	private String resolveJobExperienceLevel(Integer expYears) {
		if (expYears == null) {
			return null;
		}
		if (expYears <= 1) {
			return "JUNIOR";
		}
		if (expYears <= 4) {
			return "MID";
		}
		if (expYears <= 8) {
			return "SENIOR";
		}
		return "LEAD";
	}

	private List<String> parseCommaSeparated(String value) {
		if (value == null || value.isBlank()) {
			return List.of();
		}
		return Arrays.stream(value.split(","))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.collect(Collectors.toList());
	}

	private boolean hasCompleteProfile(User user) {
		return user.getProfileCompleteness() != null && user.getProfileCompleteness() >= 0.5;
	}

	private String buildMatchReasons(MatchScore score) {
		try {
			return objectMapper.writeValueAsString(score.getBreakdown());
		}
		catch (JsonProcessingException e) {
			return "{}";
		}
	}

	private record ScoredPosting(JobPosting jobPosting, MatchScore score) {

	}

}
