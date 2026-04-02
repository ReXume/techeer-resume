package com.techeer.backend.api.job.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Summary of a single crawl run, returned by CrawlerOrchestrationService.crawlAll().
 */
@Getter
@Builder
public class CrawlResult {

    private int newCount;

    private int skippedDuplicates;

    private int expiredCount;

    private LocalDateTime crawledAt;

    private String status; // "SUCCESS" | "PARTIAL_FAILURE" | "FAILURE"

    private String message;

    public static CrawlResult success(int newCount, int skippedDuplicates, int expiredCount) {
        return CrawlResult.builder()
            .newCount(newCount)
            .skippedDuplicates(skippedDuplicates)
            .expiredCount(expiredCount)
            .crawledAt(LocalDateTime.now())
            .status("SUCCESS")
            .message(String.format("신규 %d건 저장, %d건 중복 스킵, %d건 만료 처리",
                newCount, skippedDuplicates, expiredCount))
            .build();
    }

    public static CrawlResult failure(String reason) {
        return CrawlResult.builder()
            .newCount(0)
            .skippedDuplicates(0)
            .expiredCount(0)
            .crawledAt(LocalDateTime.now())
            .status("FAILURE")
            .message(reason)
            .build();
    }

}
