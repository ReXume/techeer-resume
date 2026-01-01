package com.techeer.backend.api.job.application.port.in;

import com.techeer.backend.api.job.dto.request.JobPostingCreateRequest;

public interface CreateJobPostingUseCase {

    Long createJobPosting(JobPostingCreateRequest request, Long userId);

}
