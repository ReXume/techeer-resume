package com.techeer.backend.api.job.application.port.out;

import com.techeer.backend.api.job.dto.CrawledJobPosting;
import java.util.List;

/**
 * Strategy interface for job-posting crawlers.
 *
 * Each platform crawler (Wanted, Saramin, …) is a separate @Component that implements
 * this interface.  Adding a new platform = creating exactly one new class.
 */
public interface JobCrawlerPort {

    /**
     * Human-readable source identifier used for logging and SourceType mapping.
     * Must match the name() of the corresponding SourceType enum value.
     * Examples: "WANTED", "SARAMIN"
     */
    String getSourceName();

    /**
     * Fetch a page of job postings from the external platform.
     *
     * @param page 0-based page index
     * @param size number of postings per page
     * @return list of crawled postings (may be empty, never null)
     */
    List<CrawledJobPosting> crawl(int page, int size);

    /**
     * Whether this crawler handles the given source name.
     *
     * @param source source name to check (e.g. "WANTED")
     * @return true if this implementation handles the source
     */
    boolean supports(String source);

}
