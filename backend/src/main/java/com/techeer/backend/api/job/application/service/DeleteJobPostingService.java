package com.techeer.backend.api.job.application.service;

import com.techeer.backend.api.company.application.port.out.LoadCompanyMemberPort;
import com.techeer.backend.api.company.domain.CompanyMember;
import com.techeer.backend.api.company.domain.CompanyRole;
import com.techeer.backend.api.job.application.port.in.DeleteJobPostingUseCase;
import com.techeer.backend.api.job.application.port.out.LoadJobPostingPort;
import com.techeer.backend.api.job.domain.JobPosting;
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
public class DeleteJobPostingService implements DeleteJobPostingUseCase {

    private final LoadJobPostingPort loadJobPostingPort;

    private final LoadUserPort loadUserPort;

    private final LoadCompanyMemberPort loadCompanyMemberPort;

    @Override
    public void deleteJobPosting(Long jobPostingId, Long userId) {
        JobPosting jobPosting = loadJobPostingPort.findById(jobPostingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_POSTING_NOT_FOUND));

        User user = loadUserPort.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        CompanyMember member = loadCompanyMemberPort.findByUserAndCompany(user, jobPosting.getCompany())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_MEMBER_NOT_FOUND));

        if (member.getRole() != CompanyRole.ADMIN) {
            throw new BusinessException(ErrorCode.COMPANY_FORBIDDEN);
        }

        jobPosting.softDelete();
    }

}
