package com.techeer.backend.api.bookmark.application.port.out;

import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.domain.User;

public interface LoadBookmarkPort {
	boolean existsByUserAndJobPosting(User user, JobPosting jobPosting);
}

