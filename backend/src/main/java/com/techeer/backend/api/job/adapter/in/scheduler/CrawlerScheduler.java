package com.techeer.backend.api.job.adapter.in.scheduler;

import com.techeer.backend.api.job.application.service.CrawlerOrchestrationService;
import com.techeer.backend.api.job.dto.CrawlResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled trigger for the job crawler pipeline.
 *
 * Runs every hour by default. Can be disabled via application.yml:
 *   crawler.enabled: false
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "crawler.enabled", havingValue = "true", matchIfMissing = true)
public class CrawlerScheduler {

    private final CrawlerOrchestrationService crawlerOrchestrationService;

    /**
     * Scheduled crawl — runs every hour (3,600,000 ms).
     * Rate can be overridden via application.yml: crawler.schedule-rate
     */
    @Scheduled(fixedRateString = "${crawler.schedule-rate:3600000}")
    public void scheduledCrawl() {
        log.info("[CrawlerScheduler] Scheduled crawl started");
        try {
            CrawlResult result = crawlerOrchestrationService.crawlAll();
            log.info("[CrawlerScheduler] Crawl finished — status={} new={} skipped={} expired={}",
                result.getStatus(),
                result.getNewCount(),
                result.getSkippedDuplicates(),
                result.getExpiredCount());
        } catch (Exception e) {
            log.error("[CrawlerScheduler] Crawl failed unexpectedly: {}", e.getMessage(), e);
        }
    }

}
