package com.techeer.backend.api.company.application.port.in;

import com.techeer.backend.api.company.dto.response.CompanyLikeInfoResponse;

public interface GetCompanyLikeUseCase {

	CompanyLikeInfoResponse getCompanyLike(Long companyLikeId);

}
