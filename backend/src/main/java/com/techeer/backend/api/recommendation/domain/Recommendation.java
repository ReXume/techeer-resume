package com.techeer.backend.api.recommendation.domain;

import com.techeer.backend.api.recommendation.domain.vo.MatchScore;
import com.techeer.backend.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "recommendations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommendation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recommendation_id")
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "job_posting_id", nullable = false)
	private Long jobPostingId;

	@Embedded
	private MatchScore matchScore;

	@Column(name = "match_reasons", columnDefinition = "TEXT")
	private String matchReasons;

	@Column(name = "is_viewed", nullable = false)
	private Boolean isViewed = false;

	@Builder
	public Recommendation(Long userId, Long jobPostingId, MatchScore matchScore, String matchReasons) {
		this.userId = userId;
		this.jobPostingId = jobPostingId;
		this.matchScore = matchScore;
		this.matchReasons = matchReasons;
		this.isViewed = false;
	}

	public void markAsViewed() {
		this.isViewed = true;
	}

}
