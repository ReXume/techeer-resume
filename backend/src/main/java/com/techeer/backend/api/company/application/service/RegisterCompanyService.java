package com.techeer.backend.api.company.application.service;

import com.techeer.backend.api.company.application.port.in.RegisterCompanyUseCase;
import com.techeer.backend.api.company.application.port.out.LoadCompanyPort;
import com.techeer.backend.api.company.application.port.out.SaveCompanyPort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.dto.request.CompanyRegisterRequest;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterCompanyService implements RegisterCompanyUseCase {

	private final LoadCompanyPort loadCompanyPort;
	private final SaveCompanyPort saveCompanyPort;

	@Override
	public Long registerCompany(CompanyRegisterRequest request) {
		if (loadCompanyPort.findByName(request.name()).isPresent()) {
			throw new BusinessException(ErrorCode.COMPANY_ALREADY_EXISTS);
		}

		Company company = Company.builder()
			.name(request.name())
			.industryDomain(request.industryDomain())
			.websiteUrl(request.websiteUrl())
			.location(request.location())
			.build();

		return saveCompanyPort.saveCompany(company).getId();
	}
}

