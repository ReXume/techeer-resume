package com.techeer.backend.api.company.application.port.in;

import com.techeer.backend.api.company.dto.request.CompanyLikeCreateRequest;

public interface LikeCompanyUseCase {

	Long likeCompany(CompanyLikeCreateRequest request, Long userId);

}
