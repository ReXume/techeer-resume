package com.techeer.backend.api.recommendation.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchScore {

	@Column(name = "match_score_value", nullable = false)
	private Double value;

	@Column(name = "skill_score")
	private Double skillScore;

	@Column(name = "experience_score")
	private Double experienceScore;

	@Column(name = "position_score")
	private Double positionScore;

	@Column(name = "location_score")
	private Double locationScore;

	@Column(name = "recency_score")
	private Double recencyScore;

	public MatchScore(Double value, Double skillScore, Double experienceScore, Double positionScore,
		Double locationScore, Double recencyScore) {
		if (value < 0.0 || value > 1.0) {
			throw new IllegalArgumentException("MatchScore value must be between 0.0 and 1.0");
		}
		this.value = value;
		this.skillScore = skillScore;
		this.experienceScore = experienceScore;
		this.positionScore = positionScore;
		this.locationScore = locationScore;
		this.recencyScore = recencyScore;
	}

	public Map<String, Double> getBreakdown() {
		Map<String, Double> breakdown = new HashMap<>();
		breakdown.put("skill", skillScore != null ? skillScore : 0.0);
		breakdown.put("experience", experienceScore != null ? experienceScore : 0.0);
		breakdown.put("position", positionScore != null ? positionScore : 0.0);
		breakdown.put("location", locationScore != null ? locationScore : 0.0);
		breakdown.put("recency", recencyScore != null ? recencyScore : 0.0);
		return breakdown;
	}

}
