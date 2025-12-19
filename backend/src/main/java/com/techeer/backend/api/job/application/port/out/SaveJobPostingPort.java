package com.techeer.backend.api.job.application.port.out;

import com.techeer.backend.api.job.domain.JobPosting;

public interface SaveJobPostingPort {

	JobPosting saveJobPosting(JobPosting jobPosting);

}
