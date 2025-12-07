package com.techeer.backend.api.job.application.service;

import com.techeer.backend.api.company.application.port.out.LoadCompanyPort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.job.application.port.in.CreateJobPostingUseCase;
import com.techeer.backend.api.job.application.port.out.SaveJobPostingPort;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.dto.request.JobPostingCreateRequest;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateJobPostingService implements CreateJobPostingUseCase {

	private final SaveJobPostingPort saveJobPostingPort;

	private final LoadCompanyPort loadCompanyPort;

	@Override
	public Long createJobPosting(JobPostingCreateRequest request) {
		Company company = loadCompanyPort.findById(request.companyId())
			.orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

		JobPosting jobPosting = JobPosting.builder()
			.company(company)
			.title(request.title())
			.contents(request.contents())
			.expYears(request.expYears())
			.build();

		return saveJobPostingPort.saveJobPosting(jobPosting).getId();
	}
}

