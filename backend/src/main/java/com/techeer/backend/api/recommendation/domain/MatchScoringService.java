package com.techeer.backend.api.recommendation.domain;

import com.techeer.backend.api.recommendation.domain.vo.MatchScore;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Pure domain service for match scoring — no Spring dependencies.
 * Formula: matchScore = 0.40 * skillOverlap + 0.25 * experienceMatch
 *                     + 0.20 * positionMatch + 0.10 * locationMatch
 *                     + 0.05 * recencyBonus
 */
public class MatchScoringService {

	private static final double SKILL_WEIGHT = 0.40;

	private static final double EXPERIENCE_WEIGHT = 0.25;

	private static final double POSITION_WEIGHT = 0.20;

	private static final double LOCATION_WEIGHT = 0.10;

	private static final double RECENCY_WEIGHT = 0.05;

	private static final int RECENCY_DAYS_THRESHOLD = 30;

	/**
	 * Calculate match score between a user profile and a job posting.
	 */
	public MatchScore calculate(
		List<String> userSkills,
		String userExperienceLevel,
		String userDesiredPosition,
		List<String> userPreferredLocations,
		List<String> jobRequiredSkills,
		String jobExperienceLevel,
		String jobPosition,
		String jobLocation,
		LocalDateTime jobPostedAt
	) {
		double skillScore = calculateSkillOverlap(userSkills, jobRequiredSkills);
		double experienceScore = calculateExperienceMatch(userExperienceLevel, jobExperienceLevel);
		double positionScore = calculatePositionMatch(userDesiredPosition, jobPosition);
		double locationScore = calculateLocationMatch(userPreferredLocations, jobLocation);
		double recencyScore = calculateRecencyBonus(jobPostedAt);

		double total = SKILL_WEIGHT * skillScore
			+ EXPERIENCE_WEIGHT * experienceScore
			+ POSITION_WEIGHT * positionScore
			+ LOCATION_WEIGHT * locationScore
			+ RECENCY_WEIGHT * recencyScore;

		return new MatchScore(total, skillScore, experienceScore, positionScore, locationScore, recencyScore);
	}

	/**
	 * skillOverlap = |userSkills ∩ requiredSkills| / |requiredSkills|
	 * Returns 0 if no required skills.
	 */
	double calculateSkillOverlap(List<String> userSkills, List<String> jobRequiredSkills) {
		if (jobRequiredSkills == null || jobRequiredSkills.isEmpty()) {
			return 0.0;
		}
		if (userSkills == null || userSkills.isEmpty()) {
			return 0.0;
		}
		Set<String> userSkillsLower = userSkills.stream()
			.map(String::toLowerCase)
			.collect(Collectors.toSet());
		long matchCount = jobRequiredSkills.stream()
			.filter(s -> userSkillsLower.contains(s.toLowerCase()))
			.count();
		return (double) matchCount / jobRequiredSkills.size();
	}

	/**
	 * experienceMatch: exact(1.0), adjacent(0.5), miss(0.0)
	 * Levels: JUNIOR, MID, SENIOR, LEAD (ordered)
	 */
	double calculateExperienceMatch(String userLevel, String jobLevel) {
		if (userLevel == null || jobLevel == null) {
			return 0.0;
		}
		if (userLevel.equalsIgnoreCase(jobLevel)) {
			return 1.0;
		}
		List<String> levels = List.of("JUNIOR", "MID", "SENIOR", "LEAD");
		int userIdx = indexOfIgnoreCase(levels, userLevel);
		int jobIdx = indexOfIgnoreCase(levels, jobLevel);
		if (userIdx == -1 || jobIdx == -1) {
			return 0.0;
		}
		return Math.abs(userIdx - jobIdx) == 1 ? 0.5 : 0.0;
	}

	/**
	 * positionMatch: exact(1.0), else(0.0)
	 */
	double calculatePositionMatch(String userDesiredPosition, String jobPosition) {
		if (userDesiredPosition == null || jobPosition == null) {
			return 0.0;
		}
		return userDesiredPosition.equalsIgnoreCase(jobPosition) ? 1.0 : 0.0;
	}

	/**
	 * locationMatch: contains check(1.0), else(0.0)
	 */
	double calculateLocationMatch(List<String> userPreferredLocations, String jobLocation) {
		if (userPreferredLocations == null || userPreferredLocations.isEmpty() || jobLocation == null) {
			return 0.0;
		}
		String jobLoc = jobLocation.toLowerCase();
		return userPreferredLocations.stream()
			.anyMatch(loc -> loc != null && jobLoc.contains(loc.toLowerCase())) ? 1.0 : 0.0;
	}

	/**
	 * recencyBonus = max(0, 1 - daysSincePosted/30)
	 */
	double calculateRecencyBonus(LocalDateTime postedAt) {
		if (postedAt == null) {
			return 0.0;
		}
		long daysSincePosted = ChronoUnit.DAYS.between(postedAt, LocalDateTime.now());
		return Math.max(0.0, 1.0 - (double) daysSincePosted / RECENCY_DAYS_THRESHOLD);
	}

	private int indexOfIgnoreCase(List<String> list, String value) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equalsIgnoreCase(value)) {
				return i;
			}
		}
		return -1;
	}

}
