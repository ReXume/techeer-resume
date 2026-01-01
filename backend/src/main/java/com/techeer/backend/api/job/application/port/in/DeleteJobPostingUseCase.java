package com.techeer.backend.api.job.application.port.in;

public interface DeleteJobPostingUseCase {

    void deleteJobPosting(Long jobPostingId, Long userId);

}
