package com.techeer.backend.api.company.domain;

import com.techeer.backend.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "companies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "company_id")
	private Long id;

	@NotNull
	@Size(max = 100)
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Size(max = 100)
	@Column(name = "industry_domain", length = 100)
	private String industryDomain;

	@Size(max = 2083)
	@Column(name = "website_url", length = 2083)
	private String websiteUrl;

	@Size(max = 200)
	@Column(name = "location", length = 200)
	private String location;

	@Builder
	public Company(String name, String industryDomain, String websiteUrl, String location) {
		this.name = name;
		this.industryDomain = industryDomain;
		this.websiteUrl = websiteUrl;
		this.location = location;
	}

	public void updateInfo(String name, String industryDomain, String websiteUrl, String location) {
		if (name != null) {
			this.name = name;
		}
		if (industryDomain != null) {
			this.industryDomain = industryDomain;
		}
		if (websiteUrl != null) {
			this.websiteUrl = websiteUrl;
		}
		if (location != null) {
			this.location = location;
		}
	}

}
