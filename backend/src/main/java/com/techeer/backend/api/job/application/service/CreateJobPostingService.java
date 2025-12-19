package com.techeer.backend.api.job.application.service;

import com.techeer.backend.api.company.application.port.out.LoadCompanyMemberPort;
import com.techeer.backend.api.company.application.port.out.LoadCompanyPort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.domain.CompanyMember;
import com.techeer.backend.api.company.domain.CompanyRole;
import com.techeer.backend.api.job.application.port.in.CreateJobPostingUseCase;
import com.techeer.backend.api.job.application.port.out.SaveJobPostingPort;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.dto.request.JobPostingCreateRequest;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.User;
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

	private final LoadUserPort loadUserPort;

	private final LoadCompanyMemberPort loadCompanyMemberPort;

	@Override
	public Long createJobPosting(JobPostingCreateRequest request, Long userId) {
		Company company = loadCompanyPort.findById(request.companyId())
			.orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

		User user = loadUserPort.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		CompanyMember member = loadCompanyMemberPort.findByUserAndCompany(user, company)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_MEMBER_NOT_FOUND));

		// 기업 관리자만 채용공고 생성 가능
		if (member.getRole() != CompanyRole.ADMIN) {
			throw new BusinessException(ErrorCode.COMPANY_FORBIDDEN);
		}

		JobPosting jobPosting = JobPosting.builder()
			.company(company)
			.title(request.title())
			.contents(request.contents())
			.expYears(request.expYears())
			.build();

		return saveJobPostingPort.saveJobPosting(jobPosting).getId();
	}

}
