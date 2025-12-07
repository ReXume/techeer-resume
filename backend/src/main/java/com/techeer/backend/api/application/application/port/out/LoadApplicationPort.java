package com.techeer.backend.api.application.application.port.out;

import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.domain.User;

public interface LoadApplicationPort {
	boolean existsByUserAndJobPosting(User user, JobPosting jobPosting);
}

