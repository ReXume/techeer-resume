package com.techeer.backend.api.job.adapter.in.web;

import com.techeer.backend.api.job.application.service.CrawlerOrchestrationService;
import com.techeer.backend.api.job.dto.CrawlResult;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin endpoints for manually triggering and inspecting the job crawler.
 *
 * These endpoints are for development / QA purposes.
 * In production, access should be restricted via Spring Security role checks.
 */
@Slf4j
@Tag(name = "CrawlerAdmin", description = "크롤러 관리자 API")
@RestController
@RequestMapping("/api/v1/admin/crawl")
@RequiredArgsConstructor
public class CrawlerAdminController {

    private final CrawlerOrchestrationService crawlerOrchestrationService;

    /** Stores the result of the last crawl run (in-memory; resets on restart). */
    private final AtomicReference<CrawlResult> lastCrawlResult = new AtomicReference<>();

    @Operation(summary = "크롤러 수동 실행", description = "크롤러를 즉시 실행하여 채용공고를 수집합니다. (테스트/관리자 전용)")
    @PostMapping
    public ResponseEntity<ApiResponse<CrawlResult>> triggerCrawl() {
        log.info("[CrawlerAdmin] Manual crawl triggered");
        CrawlResult result = crawlerOrchestrationService.crawlAll();
        lastCrawlResult.set(result);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, result));
    }

    @Operation(summary = "마지막 크롤링 결과 조회", description = "가장 최근 크롤링 실행 결과를 반환합니다.")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<CrawlResult>> getCrawlStatus() {
        CrawlResult result = lastCrawlResult.get();
        if (result == null) {
            result = CrawlResult.failure("아직 크롤링이 실행되지 않았습니다.");
        }
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, result));
    }

}
