package com.techeer.backend.api.job.application.port.out;

import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.domain.SourceType;
import java.util.List;
import java.util.Optional;

/**
 * Port for crawler-specific persistence queries.
 */
public interface FindJobPostingByExternalPort {

    /**
     * Find a job posting by its external ID and source platform.
     * Used for duplicate detection during crawl.
     *
     * @param externalId the ID on the external platform
     * @param source     the source platform
     * @return the existing posting, or empty if not found
     */
    Optional<JobPosting> findByExternalIdAndSource(String externalId, SourceType source);

    /**
     * Find all open job postings from a specific source that have a non-null deadline.
     * Used to expire overdue postings.
     *
     * @param source the source platform
     * @return list of open postings with a fixed deadline
     */
    List<JobPosting> findOpenPostingsWithDeadlineBySource(SourceType source);

}
