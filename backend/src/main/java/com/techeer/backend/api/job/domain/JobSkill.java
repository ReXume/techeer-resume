package com.techeer.backend.api.job.domain;

import com.techeer.backend.api.skill.domain.Skill;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "job_skills",
		uniqueConstraints = { @UniqueConstraint(name = "uk_job_skill", columnNames = { "jobposting_id", "skill_id" }) })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobSkill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_skill_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "jobposting_id", nullable = false)
	private JobPosting jobPosting;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "skill_id", nullable = false)
	private Skill skill;

	@Builder
	public JobSkill(JobPosting jobPosting, Skill skill) {
		this.jobPosting = jobPosting;
		this.skill = skill;
	}

}
