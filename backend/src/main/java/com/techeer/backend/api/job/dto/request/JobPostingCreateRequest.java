package com.techeer.backend.api.job.dto.request;

import com.techeer.backend.api.job.domain.DeadlineType;
import com.techeer.backend.api.job.domain.SourceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record JobPostingCreateRequest(
	@NotNull(message = "회사 ID는 필수입니다") Long companyId,

	@NotNull(message = "제목은 필수입니다") @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다") String title,

	String contents,

	Integer expYears,

	SourceType sourceType,

	String originUrl,

	String externalId,

	LocalDateTime crawledAt,

	DeadlineType deadlineType,

	Long salaryMin,

	Long salaryMax,

	String salaryCurrency
) {
}
