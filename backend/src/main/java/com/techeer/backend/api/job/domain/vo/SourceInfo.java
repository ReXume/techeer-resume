package com.techeer.backend.api.job.domain.vo;

import com.techeer.backend.api.job.domain.SourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SourceInfo {

	@Enumerated(EnumType.STRING)
	@Column(name = "source_info_type", length = 50)
	private SourceType source;

	@Column(name = "source_url", length = 2083)
	private String sourceUrl;

	@Column(name = "external_id", length = 255)
	private String externalId;

	public SourceInfo(SourceType source, String sourceUrl, String externalId) {
		this.source = source;
		this.sourceUrl = sourceUrl;
		this.externalId = externalId;
	}

	public static SourceInfo of(SourceType source, String sourceUrl, String externalId) {
		return new SourceInfo(source, sourceUrl, externalId);
	}

}
