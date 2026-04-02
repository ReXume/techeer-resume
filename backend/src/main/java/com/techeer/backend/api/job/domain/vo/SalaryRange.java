package com.techeer.backend.api.job.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalaryRange {

	@Column(name = "salary_min")
	private Long min;

	@Column(name = "salary_max")
	private Long max;

	@Column(name = "salary_currency", length = 10)
	private String currency;

	public SalaryRange(Long min, Long max, String currency) {
		this.min = min;
		this.max = max;
		this.currency = currency != null ? currency : "KRW";
	}

	public static SalaryRange of(Long min, Long max, String currency) {
		return new SalaryRange(min, max, currency);
	}

}
