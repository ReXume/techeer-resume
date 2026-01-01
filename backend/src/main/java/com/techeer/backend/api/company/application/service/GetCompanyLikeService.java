package com.techeer.backend.api.company.application.service;

import com.techeer.backend.api.company.application.port.in.GetCompanyLikeUseCase;
import com.techeer.backend.api.company.application.port.out.LoadCompanyLikePort;
import com.techeer.backend.api.company.domain.CompanyLike;
import com.techeer.backend.api.company.dto.response.CompanyLikeInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCompanyLikeService implements GetCompanyLikeUseCase {

	private final LoadCompanyLikePort loadCompanyLikePort;

	@Override
	public CompanyLikeInfoResponse getCompanyLike(Long companyLikeId) {
		CompanyLike companyLike = loadCompanyLikePort.findById(companyLikeId)
				.orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_LIKE_NOT_FOUND));

		return CompanyLikeInfoResponse.builder()
				.id(companyLike.getId())
				.companyId(companyLike.getCompany().getId())
				.companyName(companyLike.getCompany().getName())
				.createdAt(companyLike.getCreatedAt())
				.build();
	}

}
