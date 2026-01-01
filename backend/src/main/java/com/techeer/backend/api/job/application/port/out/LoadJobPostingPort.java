package com.techeer.backend.api.job.application.port.out;

import com.techeer.backend.api.job.domain.JobPosting;
import java.util.Optional;

public interface LoadJobPostingPort {

    Optional<JobPosting> findById(Long id);

}
