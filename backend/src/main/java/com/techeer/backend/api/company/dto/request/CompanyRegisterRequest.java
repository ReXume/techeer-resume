package com.techeer.backend.api.company.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompanyRegisterRequest(
		@NotBlank(message = "기업명은 필수입니다") @Size(max = 100, message = "기업명은 100자를 초과할 수 없습니다") String name,

		@Size(max = 100, message = "산업 분야는 100자를 초과할 수 없습니다") String industryDomain,

		@Size(max = 2083, message = "웹사이트 URL은 2083자를 초과할 수 없습니다") String websiteUrl,

		@Size(max = 200, message = "위치는 200자를 초과할 수 없습니다") String location) {
}
