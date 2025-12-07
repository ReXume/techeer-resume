package com.techeer.backend.api.job.application.port.in;

import com.techeer.backend.api.job.dto.response.JobPostingInfoResponse;

public interface GetJobPostingUseCase {
    JobPostingInfoResponse getJobPosting(Long jobPostingId);
}

