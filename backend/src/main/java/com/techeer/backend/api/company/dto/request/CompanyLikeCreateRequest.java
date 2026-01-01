package com.techeer.backend.api.company.dto.request;

import jakarta.validation.constraints.NotNull;

public record CompanyLikeCreateRequest(

	@NotNull(message = "기업 ID는 필수입니다")
	Long companyId,

	@NotNull(message = "사용자 ID는 필수입니다")
	Long userId

) {

}
