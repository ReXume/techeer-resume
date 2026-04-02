package com.techeer.backend.api.job.dto;

import com.techeer.backend.api.job.domain.DeadlineType;
import com.techeer.backend.api.job.domain.SourceType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Intermediate DTO representing a job posting fetched from an external crawler.
 * Converted to a JobPosting entity by CrawlerOrchestrationService.
 */
@Getter
@Builder
public class CrawledJobPosting {

    /** Unique identifier on the external platform (e.g. Wanted job ID "12345"). */
    private String externalId;

    /** Which platform this posting came from. */
    private SourceType source;

    /** Direct URL to the original job posting (for "지원하기" redirect). */
    private String sourceUrl;

    /** Company name as reported by the external platform. */
    private String companyName;

    /** Job posting title. */
    private String title;

    /** Full job description / contents. */
    private String description;

    /** Position / role name (e.g. "백엔드 개발자"). */
    private String position;

    /** Required years of experience (null = not specified). */
    private Integer experienceLevel;

    /** Skill tags (e.g. ["Java", "Spring Boot", "MySQL"]). */
    private List<String> requiredSkills;

    /** Work location (e.g. "서울 강남구"). */
    private String location;

    /** Minimum annual salary in KRW (null = not disclosed). */
    private Long salaryMin;

    /** Maximum annual salary in KRW (null = not disclosed). */
    private Long salaryMax;

    /** Absolute deadline timestamp (null when deadlineType is ROLLING or UNTIL_FILLED). */
    private LocalDateTime deadline;

    /** How the deadline is expressed (FIXED / ROLLING / UNTIL_FILLED). */
    private DeadlineType deadlineType;

}
