package com.techeer.backend.api.job.adapter.out.crawler;

import static org.assertj.core.api.Assertions.assertThat;

import com.techeer.backend.api.job.domain.SourceType;
import com.techeer.backend.api.job.dto.CrawledJobPosting;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WantedCrawlerAdapterTest {

    private WantedCrawlerAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new WantedCrawlerAdapter();
    }

    @Nested
    @DisplayName("getSourceName()")
    class GetSourceName {

        @Test
        @DisplayName("소스 이름이 WANTED 여야 한다")
        void returns_wanted() {
            assertThat(adapter.getSourceName()).isEqualTo("WANTED");
        }
    }

    @Nested
    @DisplayName("supports()")
    class Supports {

        @Test
        @DisplayName("WANTED 소스를 지원해야 한다")
        void supports_wanted() {
            assertThat(adapter.supports("WANTED")).isTrue();
        }

        @Test
        @DisplayName("대소문자 구분 없이 wanted를 지원해야 한다")
        void supports_wanted_case_insensitive() {
            assertThat(adapter.supports("wanted")).isTrue();
        }

        @Test
        @DisplayName("다른 소스는 지원하지 않아야 한다")
        void does_not_support_other_sources() {
            assertThat(adapter.supports("SARAMIN")).isFalse();
            assertThat(adapter.supports("LINKEDIN")).isFalse();
        }
    }

    @Nested
    @DisplayName("crawl()")
    class Crawl {

        @Test
        @DisplayName("첫 페이지 크롤링 시 데이터를 반환해야 한다")
        void returns_data_for_first_page() {
            List<CrawledJobPosting> result = adapter.crawl(0, 5);

            assertThat(result).isNotEmpty();
            assertThat(result.size()).isLessThanOrEqualTo(5);
        }

        @Test
        @DisplayName("크롤링된 공고의 필수 필드가 채워져 있어야 한다")
        void postings_have_required_fields() {
            List<CrawledJobPosting> result = adapter.crawl(0, 10);

            for (CrawledJobPosting posting : result) {
                assertThat(posting.getExternalId()).isNotBlank();
                assertThat(posting.getSource()).isEqualTo(SourceType.WANTED);
                assertThat(posting.getSourceUrl()).startsWith("https://www.wanted.co.kr/wd/");
                assertThat(posting.getCompanyName()).isNotBlank();
                assertThat(posting.getTitle()).isNotBlank();
                assertThat(posting.getDescription()).isNotBlank();
                assertThat(posting.getPosition()).isNotBlank();
                assertThat(posting.getDeadlineType()).isNotNull();
            }
        }

        @Test
        @DisplayName("externalId는 중복되지 않아야 한다")
        void external_ids_are_unique() {
            List<CrawledJobPosting> result = adapter.crawl(0, 50);

            long uniqueIds = result.stream()
                .map(CrawledJobPosting::getExternalId)
                .distinct()
                .count();

            assertThat(uniqueIds).isEqualTo(result.size());
        }

        @Test
        @DisplayName("범위를 벗어난 페이지는 빈 목록을 반환해야 한다")
        void returns_empty_for_out_of_bounds_page() {
            List<CrawledJobPosting> result = adapter.crawl(999, 10);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("size보다 많은 결과를 반환하지 않아야 한다")
        void does_not_exceed_requested_size() {
            int size = 3;
            List<CrawledJobPosting> result = adapter.crawl(0, size);

            assertThat(result.size()).isLessThanOrEqualTo(size);
        }

        @Test
        @DisplayName("급여 정보가 존재하면 최소값이 최대값보다 작아야 한다")
        void salary_min_is_less_than_max() {
            List<CrawledJobPosting> result = adapter.crawl(0, 50);

            for (CrawledJobPosting posting : result) {
                if (posting.getSalaryMin() != null && posting.getSalaryMax() != null) {
                    assertThat(posting.getSalaryMin()).isLessThanOrEqualTo(posting.getSalaryMax());
                }
            }
        }

        @Test
        @DisplayName("FIXED 마감일 공고는 deadline 값이 있어야 한다")
        void fixed_deadline_postings_have_deadline() {
            List<CrawledJobPosting> result = adapter.crawl(0, 50);

            result.stream()
                .filter(p -> com.techeer.backend.api.job.domain.DeadlineType.FIXED.equals(p.getDeadlineType()))
                .forEach(p -> assertThat(p.getDeadline()).isNotNull());
        }
    }

}
