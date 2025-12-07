package com.techeer.backend.api.job.service;

import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.repository.CompanyRepository;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.dto.request.JobPostingCreateRequest;
import com.techeer.backend.api.job.repository.JobPostingRepository;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostingService {

	private final JobPostingRepository jobPostingRepository;
	private final CompanyRepository companyRepository;

	@Transactional
	public Long createJobPosting(JobPostingCreateRequest request) {
		Company company = companyRepository.findById(request.companyId())
			.orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

		JobPosting jobPosting = JobPosting.builder()
			.company(company)
			.title(request.title())
			.contents(request.contents())
			.expYears(request.expYears())
			.build();

		return jobPostingRepository.save(jobPosting).getId();
	}

	public JobPosting getJobPosting(Long id) {
		return jobPostingRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.JOB_POSTING_NOT_FOUND));
	}
}

