package com.techeer.backend.api.job.application.service;

import com.techeer.backend.api.job.application.port.in.GetJobPostingUseCase;
import com.techeer.backend.api.job.application.port.out.LoadJobPostingPort;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.dto.response.JobPostingInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetJobPostingService implements GetJobPostingUseCase {

    private final LoadJobPostingPort loadJobPostingPort;

    @Override
    public JobPostingInfoResponse getJobPosting(Long jobPostingId) {
        JobPosting jobPosting = loadJobPostingPort.findById(jobPostingId)
            .orElseThrow(() -> new BusinessException(ErrorCode.JOB_POSTING_NOT_FOUND));

        return JobPostingInfoResponse.builder()
            .id(jobPosting.getId())
            .companyId(jobPosting.getCompany().getId())
            .companyName(jobPosting.getCompany().getName())
            .title(jobPosting.getTitle())
            .contents(jobPosting.getContents())
            .expYears(jobPosting.getExpYears())
            .status(jobPosting.getStatus().name())
            .build();
    }
}

