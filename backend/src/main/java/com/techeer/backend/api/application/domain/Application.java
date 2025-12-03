package com.techeer.backend.api.application.domain;

import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "applications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "application_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "jobposting_id", nullable = false)
	private JobPosting jobPosting;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private ApplicationStatus status = ApplicationStatus.APPLIED;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Builder
	public Application(User user, JobPosting jobPosting, ApplicationStatus status) {
		this.user = user;
		this.jobPosting = jobPosting;
		this.status = status != null ? status : ApplicationStatus.APPLIED;
	}

	public void updateStatus(ApplicationStatus status) {
		if (status != null) {
			this.status = status;
		}
	}

	public void markAsViewed() {
		this.status = ApplicationStatus.VIEWED;
	}

	public void markAsPassed() {
		this.status = ApplicationStatus.PASSED;
	}

	public void markAsRejected() {
		this.status = ApplicationStatus.REJECTED;
	}

}
