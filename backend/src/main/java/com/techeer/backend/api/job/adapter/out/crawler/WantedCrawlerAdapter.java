package com.techeer.backend.api.job.adapter.out.crawler;

import com.techeer.backend.api.job.application.port.out.JobCrawlerPort;
import com.techeer.backend.api.job.domain.DeadlineType;
import com.techeer.backend.api.job.domain.SourceType;
import com.techeer.backend.api.job.dto.CrawledJobPosting;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Wanted platform crawler adapter.
 *
 * MVP: returns realistic mock data so the rest of the pipeline can be tested end-to-end
 * without a real API key.
 *
 * To replace with the real Wanted API:
 *   1. Inject RestTemplate / WebClient
 *   2. Replace the body of fetchFromWantedApi() with an actual HTTP call to
 *      https://www.wanted.co.kr/api/v4/jobs?country=kr&job_sort=job.latest_order&limit={size}&offset={page*size}
 *   3. Map the JSON response to CrawledJobPosting using a mapper
 *   4. Remove the mock data below
 */
@Slf4j
@Component
public class WantedCrawlerAdapter implements JobCrawlerPort {

    private static final String SOURCE_NAME = "WANTED";

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public boolean supports(String source) {
        return SOURCE_NAME.equalsIgnoreCase(source);
    }

    @Override
    public List<CrawledJobPosting> crawl(int page, int size) {
        log.info("[Wanted] Crawling page={}, size={}", page, size);

        // ------------------------------------------------------------------
        // REAL API CALL WOULD GO HERE:
        //   List<WantedJobDto> raw = fetchFromWantedApi(page, size);
        //   return raw.stream().map(this::toDto).toList();
        // ------------------------------------------------------------------

        return generateMockPostings(page, size);
    }

    // -----------------------------------------------------------------------
    // Mock data — realistic Korean job postings for backend/frontend positions
    // -----------------------------------------------------------------------

    private List<CrawledJobPosting> generateMockPostings(int page, int size) {
        List<CrawledJobPosting> result = new ArrayList<>();

        List<MockJob> mockJobs = List.of(
            new MockJob("wanted-10001", "카카오", "백엔드 개발자 (Java/Spring)",
                "카카오 서비스의 백엔드 시스템을 설계하고 개발합니다.\n\n[주요 업무]\n- 대용량 트래픽 처리 시스템 개발\n- MSA 기반 백엔드 서비스 개발\n- 성능 최적화 및 기술 부채 해소",
                "백엔드 개발자", 3, List.of("Java", "Spring Boot", "MySQL", "Redis", "Kafka"),
                "서울 성남시 분당구", 60_000_000L, 100_000_000L, DeadlineType.ROLLING),
            new MockJob("wanted-10002", "네이버", "프론트엔드 개발자 (React)",
                "네이버 서비스 UI/UX 개발을 담당합니다.\n\n[주요 업무]\n- React 기반 웹 애플리케이션 개발\n- 성능 최적화 및 사용자 경험 개선\n- 디자인 시스템 구축 및 유지보수",
                "프론트엔드 개발자", 2, List.of("React", "TypeScript", "Next.js", "GraphQL"),
                "서울 성남시 분당구", 55_000_000L, 90_000_000L, DeadlineType.FIXED),
            new MockJob("wanted-10003", "토스", "백엔드 개발자 (Kotlin/Spring)",
                "토스 금융 서비스의 핵심 백엔드를 개발합니다.\n\n[주요 업무]\n- 금융 도메인 서비스 개발\n- 고가용성 시스템 설계 및 구현\n- 코드 리뷰 및 기술 문화 기여",
                "백엔드 개발자", 4, List.of("Kotlin", "Spring Boot", "JPA", "PostgreSQL", "AWS"),
                "서울 강남구", 70_000_000L, 120_000_000L, DeadlineType.ROLLING),
            new MockJob("wanted-10004", "쿠팡", "데이터 엔지니어",
                "쿠팡의 데이터 파이프라인을 구축하고 운영합니다.\n\n[주요 업무]\n- 대규모 데이터 파이프라인 설계 및 구현\n- Spark/Flink 기반 실시간 데이터 처리\n- 데이터 품질 모니터링 시스템 구축",
                "데이터 엔지니어", 3, List.of("Python", "Spark", "Kafka", "Airflow", "AWS"),
                "서울 송파구", 65_000_000L, 110_000_000L, DeadlineType.ROLLING),
            new MockJob("wanted-10005", "배달의민족", "iOS 개발자",
                "배달의민족 iOS 앱 개발 및 유지보수를 담당합니다.\n\n[주요 업무]\n- Swift 기반 iOS 앱 개발\n- 앱 성능 최적화 및 버그 수정\n- 신규 기능 기획 및 구현",
                "iOS 개발자", 2, List.of("Swift", "UIKit", "SwiftUI", "RxSwift"),
                "서울 송파구", 55_000_000L, 90_000_000L, DeadlineType.FIXED),
            new MockJob("wanted-10006", "당근마켓", "풀스택 개발자",
                "당근마켓의 중고거래 플랫폼 개발에 참여합니다.\n\n[주요 업무]\n- Ruby on Rails / React 기반 서비스 개발\n- 지역 기반 서비스 기능 구현\n- 사용자 경험 개선을 위한 A/B 테스트",
                "풀스택 개발자", 1, List.of("Ruby on Rails", "React", "TypeScript", "PostgreSQL"),
                "서울 판교", 50_000_000L, 85_000_000L, DeadlineType.ROLLING),
            new MockJob("wanted-10007", "라인", "DevOps / SRE 엔지니어",
                "라인 글로벌 서비스의 인프라를 운영합니다.\n\n[주요 업무]\n- Kubernetes 기반 컨테이너 플랫폼 운영\n- CI/CD 파이프라인 구축\n- 서비스 모니터링 및 장애 대응",
                "DevOps / SRE", 3, List.of("Kubernetes", "Terraform", "Prometheus", "Grafana", "Go"),
                "서울 신도림", 65_000_000L, 105_000_000L, DeadlineType.ROLLING),
            new MockJob("wanted-10008", "카카오페이", "보안 엔지니어",
                "카카오페이 금융 서비스 보안을 담당합니다.\n\n[주요 업무]\n- 애플리케이션 보안 취약점 분석 및 대응\n- 보안 정책 수립 및 시스템 구축\n- 침해사고 분석 및 대응",
                "보안 엔지니어", 5, List.of("Python", "Java", "네트워크 보안", "암호화"),
                "서울 강남구", 70_000_000L, 115_000_000L, DeadlineType.UNTIL_FILLED)
        );

        int fromIndex = page * size;
        if (fromIndex >= mockJobs.size()) {
            return result;
        }

        int toIndex = Math.min(fromIndex + size, mockJobs.size());
        for (MockJob job : mockJobs.subList(fromIndex, toIndex)) {
            result.add(toDto(job));
        }

        return result;
    }

    private CrawledJobPosting toDto(MockJob job) {
        LocalDateTime deadline = job.deadlineType() == DeadlineType.FIXED
            ? LocalDateTime.now().plusDays(30)
            : null;

        return CrawledJobPosting.builder()
            .externalId(job.externalId())
            .source(SourceType.WANTED)
            .sourceUrl("https://www.wanted.co.kr/wd/" + job.externalId().replace("wanted-", ""))
            .companyName(job.companyName())
            .title(job.title())
            .description(job.description())
            .position(job.position())
            .experienceLevel(job.experienceLevel())
            .requiredSkills(job.requiredSkills())
            .location(job.location())
            .salaryMin(job.salaryMin())
            .salaryMax(job.salaryMax())
            .deadline(deadline)
            .deadlineType(job.deadlineType())
            .build();
    }

    private record MockJob(
        String externalId,
        String companyName,
        String title,
        String description,
        String position,
        Integer experienceLevel,
        List<String> requiredSkills,
        String location,
        Long salaryMin,
        Long salaryMax,
        DeadlineType deadlineType
    ) {}

}
