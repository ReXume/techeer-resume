package com.techeer.backend.api.job.domain;

import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.global.common.BaseEntity;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "job_postings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPosting extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "jobposting_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", nullable = false)
	private Company company;

	@NotNull
	@Size(max = 200)
	@Column(name = "title", nullable = false, length = 200)
	private String title;

	@Column(name = "contents", columnDefinition = "MEDIUMTEXT")
	private String contents;

	@Column(name = "exp_years", columnDefinition = "TINYINT COMMENT '요구 경력 년수'")
	private Integer expYears;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "source_type", nullable = false)
	private SourceType sourceType = SourceType.DIRECT;

	@Size(max = 2083)
	@Column(name = "origin_url", length = 2083)
	private String originUrl;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private JobPostingStatus status = JobPostingStatus.OPEN;

	@Builder
	public JobPosting(Company company, String title, String contents, Integer expYears, SourceType sourceType,
			String originUrl, JobPostingStatus status) {
		this.company = company;
		this.title = title;
		this.contents = contents;
		this.expYears = expYears;
		this.sourceType = sourceType != null ? sourceType : SourceType.DIRECT;
		this.originUrl = originUrl;
		this.status = status != null ? status : JobPostingStatus.OPEN;
	}

	public void updateJobPosting(String title, String contents, Integer expYears, JobPostingStatus status) {
		if (title != null) {
			this.title = title;
		}
		if (contents != null) {
			this.contents = contents;
		}
		if (expYears != null) {
			this.expYears = expYears;
		}
		if (status != null) {
			this.status = status;
		}
	}

	public void close() {
		this.status = JobPostingStatus.CLOSED;
	}

	public void open() {
		this.status = JobPostingStatus.OPEN;
	}

}
