package com.techeer.backend.api.job.application.port.in;

import com.techeer.backend.api.job.dto.request.JobPostingUpdateRequest;

public interface UpdateJobPostingUseCase {

	void updateJobPosting(Long jobPostingId, JobPostingUpdateRequest request, Long userId);

}
