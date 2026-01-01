package com.techeer.backend.api.company.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CompanyLikeInfoResponse(

	Long id,

	Long companyId,

	String companyName,

	LocalDateTime createdAt

) {

}
