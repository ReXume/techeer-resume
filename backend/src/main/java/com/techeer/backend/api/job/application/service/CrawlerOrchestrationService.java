package com.techeer.backend.api.job.application.service;

import com.techeer.backend.api.company.application.port.out.LoadCompanyPort;
import com.techeer.backend.api.company.application.port.out.SaveCompanyPort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.job.application.port.out.FindJobPostingByExternalPort;
import com.techeer.backend.api.job.application.port.out.JobCrawlerPort;
import com.techeer.backend.api.job.application.port.out.SaveJobPostingPort;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.domain.SourceType;
import com.techeer.backend.api.job.domain.vo.SalaryRange;
import com.techeer.backend.api.job.domain.vo.SourceInfo;
import com.techeer.backend.api.job.dto.CrawlResult;
import com.techeer.backend.api.job.dto.CrawledJobPosting;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates all registered crawlers (Strategy pattern).
 *
 * Iterates every JobCrawlerPort bean, fetches job postings, converts them to
 * JobPosting entities, and persists them — skipping duplicates and expiring
 * postings whose deadline has passed.
 *
 * Adding a new platform: create one new @Component implementing JobCrawlerPort.
 * No changes required here.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerOrchestrationService {

    /** All registered crawler adapters injected automatically by Spring. */
    private final List<JobCrawlerPort> crawlers;

    private final SaveJobPostingPort saveJobPostingPort;
    private final FindJobPostingByExternalPort findJobPostingByExternalPort;
    private final LoadCompanyPort loadCompanyPort;
    private final SaveCompanyPort saveCompanyPort;

    private static final int CRAWL_PAGE = 0;
    private static final int CRAWL_SIZE = 50;

    /**
     * Run all crawlers in sequence and persist results.
     *
     * @return summary of the crawl run
     */
    @Transactional
    public CrawlResult crawlAll() {
        log.info("[Crawler] Starting crawl run with {} crawler(s)", crawlers.size());

        int newCount = 0;
        int skippedDuplicates = 0;
        int expiredCount = 0;

        try {
            for (JobCrawlerPort crawler : crawlers) {
                String sourceName = crawler.getSourceName();
                log.info("[Crawler] Running crawler: {}", sourceName);

                List<CrawledJobPosting> postings = crawler.crawl(CRAWL_PAGE, CRAWL_SIZE);
                log.info("[Crawler] {} returned {} posting(s)", sourceName, postings.size());

                for (CrawledJobPosting dto : postings) {
                    boolean isDuplicate = findJobPostingByExternalPort
                        .findByExternalIdAndSource(dto.getExternalId(), dto.getSource())
                        .isPresent();

                    if (isDuplicate) {
                        skippedDuplicates++;
                        log.debug("[Crawler] Skipped duplicate: source={} externalId={}",
                            sourceName, dto.getExternalId());
                        continue;
                    }

                    JobPosting jobPosting = convertToEntity(dto);
                    saveJobPostingPort.saveJobPosting(jobPosting);
                    newCount++;
                    log.debug("[Crawler] Saved: source={} externalId={} title={}",
                        sourceName, dto.getExternalId(), dto.getTitle());
                }

                // Expire postings whose fixed deadline has passed
                SourceType sourceType = SourceType.valueOf(sourceName);
                List<JobPosting> openPostings =
                    findJobPostingByExternalPort.findOpenPostingsWithDeadlineBySource(sourceType);

                LocalDateTime now = LocalDateTime.now();
                for (JobPosting posting : openPostings) {
                    // crawledAt is used as a proxy: if posting was crawled and deadline
                    // is in the past (we track via deadlineType=FIXED + crawledAt offset)
                    // Real logic: compare actual deadline field once it's mapped to entity
                    if (posting.getCrawledAt() != null && posting.getCrawledAt().plusDays(30).isBefore(now)) {
                        posting.expire();
                        expiredCount++;
                        log.info("[Crawler] Expired posting id={}", posting.getId());
                    }
                }
            }

            CrawlResult result = CrawlResult.success(newCount, skippedDuplicates, expiredCount);
            log.info("[Crawler] Crawl complete — {}", result.getMessage());
            return result;

        } catch (Exception e) {
            log.error("[Crawler] Crawl failed: {}", e.getMessage(), e);
            return CrawlResult.failure(e.getMessage());
        }
    }

    /**
     * Convert a CrawledJobPosting DTO to a JobPosting entity.
     *
     * Company is looked up by name; created on-the-fly if not found (crawl-only companies
     * will have minimal data — a human admin can enrich them later).
     */
    private JobPosting convertToEntity(CrawledJobPosting dto) {
        Company company = loadCompanyPort.findByName(dto.getCompanyName())
            .orElseGet(() -> {
                log.info("[Crawler] Auto-creating company: {}", dto.getCompanyName());
                return saveCompanyPort.saveCompany(
                    Company.builder()
                        .name(dto.getCompanyName())
                        .build()
                );
            });

        SalaryRange salaryRange = null;
        if (dto.getSalaryMin() != null || dto.getSalaryMax() != null) {
            salaryRange = SalaryRange.of(dto.getSalaryMin(), dto.getSalaryMax(), "KRW");
        }

        SourceInfo sourceInfo = SourceInfo.of(dto.getSource(), dto.getSourceUrl(), dto.getExternalId());

        return JobPosting.builder()
            .company(company)
            .title(dto.getTitle())
            .contents(dto.getDescription())
            .expYears(dto.getExperienceLevel())
            .sourceType(dto.getSource())
            .originUrl(dto.getSourceUrl())
            .externalId(dto.getExternalId())
            .crawledAt(LocalDateTime.now())
            .deadlineType(dto.getDeadlineType())
            .salaryRange(salaryRange)
            .sourceInfo(sourceInfo)
            .build();
    }

}
