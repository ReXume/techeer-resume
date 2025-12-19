package com.techeer.backend.api.company.application.service;

import com.techeer.backend.api.company.application.port.in.GetCompanyUseCase;
import com.techeer.backend.api.company.application.port.out.LoadCompanyPort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.dto.response.CompanyInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCompanyService implements GetCompanyUseCase {

	private final LoadCompanyPort loadCompanyPort;

	@Override
	public CompanyInfoResponse getCompany(Long companyId) {
		Company company = loadCompanyPort.findById(companyId)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

		return CompanyInfoResponse.builder()
			.id(company.getId())
			.name(company.getName())
			.industryDomain(company.getIndustryDomain())
			.websiteUrl(company.getWebsiteUrl())
			.location(company.getLocation())
			.build();
	}

}
