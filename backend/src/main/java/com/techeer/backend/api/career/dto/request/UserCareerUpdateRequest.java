package com.techeer.backend.api.career.dto.request;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UserCareerUpdateRequest(

		@Size(max = 100, message = "회사명은 100자를 초과할 수 없습니다")
		String companyName,
		String jobTitle,
		Boolean isCurrent,
		LocalDate startDate,
		LocalDate endDate

) {
}
