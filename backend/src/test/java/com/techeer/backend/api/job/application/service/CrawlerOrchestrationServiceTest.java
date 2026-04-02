package com.techeer.backend.api.job.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.techeer.backend.api.company.application.port.out.LoadCompanyPort;
import com.techeer.backend.api.company.application.port.out.SaveCompanyPort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.job.application.port.out.FindJobPostingByExternalPort;
import com.techeer.backend.api.job.application.port.out.JobCrawlerPort;
import com.techeer.backend.api.job.application.port.out.SaveJobPostingPort;
import com.techeer.backend.api.job.domain.DeadlineType;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.domain.JobPostingStatus;
import com.techeer.backend.api.job.domain.SourceType;
import com.techeer.backend.api.job.dto.CrawlResult;
import com.techeer.backend.api.job.dto.CrawledJobPosting;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrawlerOrchestrationServiceTest {

    @Mock
    private JobCrawlerPort crawlerPort;

    @Mock
    private SaveJobPostingPort saveJobPostingPort;

    @Mock
    private FindJobPostingByExternalPort findJobPostingByExternalPort;

    @Mock
    private LoadCompanyPort loadCompanyPort;

    @Mock
    private SaveCompanyPort saveCompanyPort;

    private CrawlerOrchestrationService service;

    @BeforeEach
    void setUp() {
        service = new CrawlerOrchestrationService(
            List.of(crawlerPort),
            saveJobPostingPort,
            findJobPostingByExternalPort,
            loadCompanyPort,
            saveCompanyPort
        );
    }

    private CrawledJobPosting buildDto(String externalId, String companyName) {
        return CrawledJobPosting.builder()
            .externalId(externalId)
            .source(SourceType.WANTED)
            .sourceUrl("https://www.wanted.co.kr/wd/" + externalId)
            .companyName(companyName)
            .title("백엔드 개발자")
            .description("백엔드 개발 담당")
            .position("백엔드 개발자")
            .experienceLevel(2)
            .requiredSkills(List.of("Java", "Spring Boot"))
            .location("서울 강남구")
            .salaryMin(50_000_000L)
            .salaryMax(80_000_000L)
            .deadlineType(DeadlineType.ROLLING)
            .build();
    }

    @Nested
    @DisplayName("crawlAll() — 신규 채용공고 저장")
    class NewPostings {

        @Test
        @DisplayName("신규 채용공고는 저장되어야 한다")
        void saves_new_postings() {
            // Given
            CrawledJobPosting dto = buildDto("ext-001", "카카오");
            Company company = Company.builder().name("카카오").build();
            JobPosting saved = JobPosting.builder()
                .company(company).title("백엔드 개발자").build();

            given(crawlerPort.getSourceName()).willReturn("WANTED");
            given(crawlerPort.crawl(0, 50)).willReturn(List.of(dto));
            given(findJobPostingByExternalPort.findByExternalIdAndSource("ext-001", SourceType.WANTED))
                .willReturn(Optional.empty());
            given(loadCompanyPort.findByName("카카오")).willReturn(Optional.of(company));
            given(saveJobPostingPort.saveJobPosting(any(JobPosting.class))).willReturn(saved);
            given(findJobPostingByExternalPort.findOpenPostingsWithDeadlineBySource(SourceType.WANTED))
                .willReturn(List.of());

            // When
            CrawlResult result = service.crawlAll();

            // Then
            assertThat(result.getStatus()).isEqualTo("SUCCESS");
            assertThat(result.getNewCount()).isEqualTo(1);
            assertThat(result.getSkippedDuplicates()).isEqualTo(0);
            verify(saveJobPostingPort, times(1)).saveJobPosting(any(JobPosting.class));
        }

        @Test
        @DisplayName("회사가 없으면 새로 생성해야 한다")
        void creates_company_if_not_found() {
            // Given
            CrawledJobPosting dto = buildDto("ext-002", "신규스타트업");
            Company newCompany = Company.builder().name("신규스타트업").build();
            JobPosting saved = JobPosting.builder()
                .company(newCompany).title("백엔드 개발자").build();

            given(crawlerPort.getSourceName()).willReturn("WANTED");
            given(crawlerPort.crawl(0, 50)).willReturn(List.of(dto));
            given(findJobPostingByExternalPort.findByExternalIdAndSource("ext-002", SourceType.WANTED))
                .willReturn(Optional.empty());
            given(loadCompanyPort.findByName("신규스타트업")).willReturn(Optional.empty());
            given(saveCompanyPort.saveCompany(any(Company.class))).willReturn(newCompany);
            given(saveJobPostingPort.saveJobPosting(any(JobPosting.class))).willReturn(saved);
            given(findJobPostingByExternalPort.findOpenPostingsWithDeadlineBySource(SourceType.WANTED))
                .willReturn(List.of());

            // When
            CrawlResult result = service.crawlAll();

            // Then
            assertThat(result.getNewCount()).isEqualTo(1);
            verify(saveCompanyPort, times(1)).saveCompany(any(Company.class));
        }
    }

    @Nested
    @DisplayName("crawlAll() — 중복 감지")
    class DuplicateDetection {

        @Test
        @DisplayName("이미 존재하는 externalId+source 조합은 저장하지 않아야 한다")
        void skips_duplicate_postings() {
            // Given
            CrawledJobPosting dto = buildDto("ext-already-exists", "카카오");
            Company company = Company.builder().name("카카오").build();
            JobPosting existing = JobPosting.builder()
                .company(company).title("기존 공고").build();

            given(crawlerPort.getSourceName()).willReturn("WANTED");
            given(crawlerPort.crawl(0, 50)).willReturn(List.of(dto));
            given(findJobPostingByExternalPort.findByExternalIdAndSource("ext-already-exists", SourceType.WANTED))
                .willReturn(Optional.of(existing));
            given(findJobPostingByExternalPort.findOpenPostingsWithDeadlineBySource(SourceType.WANTED))
                .willReturn(List.of());

            // When
            CrawlResult result = service.crawlAll();

            // Then
            assertThat(result.getStatus()).isEqualTo("SUCCESS");
            assertThat(result.getSkippedDuplicates()).isEqualTo(1);
            assertThat(result.getNewCount()).isEqualTo(0);
            verify(saveJobPostingPort, never()).saveJobPosting(any());
        }

        @Test
        @DisplayName("신규와 중복이 섞인 경우 신규만 저장해야 한다")
        void saves_only_new_when_mixed() {
            // Given
            CrawledJobPosting newDto = buildDto("ext-new", "네이버");
            CrawledJobPosting dupDto = buildDto("ext-dup", "카카오");
            Company company = Company.builder().name("네이버").build();
            JobPosting existing = JobPosting.builder()
                .company(company).title("기존").build();
            JobPosting saved = JobPosting.builder()
                .company(company).title("신규").build();

            given(crawlerPort.getSourceName()).willReturn("WANTED");
            given(crawlerPort.crawl(0, 50)).willReturn(List.of(newDto, dupDto));
            given(findJobPostingByExternalPort.findByExternalIdAndSource("ext-new", SourceType.WANTED))
                .willReturn(Optional.empty());
            given(findJobPostingByExternalPort.findByExternalIdAndSource("ext-dup", SourceType.WANTED))
                .willReturn(Optional.of(existing));
            given(loadCompanyPort.findByName("네이버")).willReturn(Optional.of(company));
            given(saveJobPostingPort.saveJobPosting(any())).willReturn(saved);
            given(findJobPostingByExternalPort.findOpenPostingsWithDeadlineBySource(SourceType.WANTED))
                .willReturn(List.of());

            // When
            CrawlResult result = service.crawlAll();

            // Then
            assertThat(result.getNewCount()).isEqualTo(1);
            assertThat(result.getSkippedDuplicates()).isEqualTo(1);
            verify(saveJobPostingPort, times(1)).saveJobPosting(any());
        }
    }

    @Nested
    @DisplayName("crawlAll() — 만료 처리")
    class ExpiryManagement {

        @Test
        @DisplayName("크롤러가 빈 목록을 반환해도 오류 없이 성공해야 한다")
        void handles_empty_crawl_result() {
            // Given
            given(crawlerPort.getSourceName()).willReturn("WANTED");
            given(crawlerPort.crawl(0, 50)).willReturn(List.of());
            given(findJobPostingByExternalPort.findOpenPostingsWithDeadlineBySource(SourceType.WANTED))
                .willReturn(List.of());

            // When
            CrawlResult result = service.crawlAll();

            // Then
            assertThat(result.getStatus()).isEqualTo("SUCCESS");
            assertThat(result.getNewCount()).isEqualTo(0);
            assertThat(result.getSkippedDuplicates()).isEqualTo(0);
            assertThat(result.getExpiredCount()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("crawlAll() — 오류 처리")
    class ErrorHandling {

        @Test
        @DisplayName("크롤러 예외 발생 시 FAILURE 결과를 반환해야 한다")
        void returns_failure_on_exception() {
            // Given
            given(crawlerPort.getSourceName()).willReturn("WANTED");
            given(crawlerPort.crawl(0, 50)).willThrow(new RuntimeException("API 연결 실패"));

            // When
            CrawlResult result = service.crawlAll();

            // Then
            assertThat(result.getStatus()).isEqualTo("FAILURE");
            assertThat(result.getMessage()).contains("API 연결 실패");
        }
    }

}
