package com.techeer.backend.api.company.service;

import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.dto.request.CompanyRegisterRequest;
import com.techeer.backend.api.company.repository.CompanyRepository;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

	private final CompanyRepository companyRepository;

	@Transactional
	public Long registerCompany(CompanyRegisterRequest request) {
		// 기업명 중복 체크
		if (companyRepository.findByName(request.name()).isPresent()) {
			throw new BusinessException(ErrorCode.COMPANY_ALREADY_EXISTS);
		}

		Company company = Company.builder()
			.name(request.name())
			.industryDomain(request.industryDomain())
			.websiteUrl(request.websiteUrl())
			.location(request.location())
			.build();

		Company savedCompany = companyRepository.save(company);
		return savedCompany.getId();
	}
}
