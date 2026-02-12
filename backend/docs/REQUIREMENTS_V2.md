# Techeer Resume - 채용 플랫폼 요구사항 분석서 v2.0

> **문서 분류:** 기밀 / 내부용
> **작성일:** 2026-02-13
> **작성자:** CTO Office, Strategic Architecture Division
> **검토자:** VP of Engineering, Director of Platform
> **상태:** DRAFT - 이해관계자 검토 대기

---

## 목차

1. [Executive Summary](#1-executive-summary)
2. [As-Is Analysis](#2-as-is-analysis)
3. [Gap Analysis](#3-gap-analysis)
4. [Functional Requirements](#4-functional-requirements)
5. [Non-Functional Requirements](#5-non-functional-requirements)
6. [Data Architecture](#6-data-architecture)
7. [API Specification](#7-api-specification)
8. [Technology Stack Additions](#8-technology-stack-additions)
9. [Implementation Phases](#9-implementation-phases)
10. [Risk Analysis](#10-risk-analysis)
11. [Success Metrics (KPIs)](#11-success-metrics-kpis)
12. [Acceptance Criteria](#12-acceptance-criteria)

---

## 1. Executive Summary

### 1.1 프로젝트 비전

Techeer Resume는 한국 IT 채용 시장을 위한 차세대 지능형 채용 플랫폼이다. 단순한 구인/구직 매칭을 넘어, 채용공고 크롤링을 통한 시장 데이터 수집, AI 기반 맞춤형 추천, 실시간 시장 트렌드 분석을 제공하여 구직자와 기업 모두에게 데이터 기반 의사결정을 지원하는 플랫폼을 목표로 한다.

### 1.2 현재 상태 요약

플랫폼은 핵심 CRUD 인프라를 갖추고 있다. 13개 컨트롤러, 40개 이상의 API 엔드포인트가 구현되어 있으며, 헥사고날 아키텍처(Ports & Adapters) 기반으로 Java 21 + Spring Boot 3.5.6 위에서 동작한다. 사용자 관리, 기업 관리, 채용공고 CRUD, 지원 관리, 문서(이력서/포트폴리오) 관리, 스킬 시스템 등 기본 도메인이 완성되었다.

그러나 **플랫폼의 핵심 가치 제안인 "지능형 채용"에 해당하는 기능은 아직 구현되지 않았다.** 채용공고 목록 조회/검색 API가 부재하여 사용자가 채용공고를 탐색할 수 없고, 크롤링 시스템, 추천 엔진, 트렌드 분석은 전무하다. `SourceType.CRAWLED` enum이 존재하지만 이를 활용하는 크롤링 코드는 없다.

### 1.3 핵심 목표

| 목표 | 설명 | 우선순위 |
|------|------|---------|
| 채용공고 크롤링 | 사람인, 잡코리아, 원티드에서 채용공고 수집 | P0 |
| 검색 및 필터링 | 사용자가 채용공고를 탐색/검색할 수 있는 기능 | P0 |
| 채용 시장 트렌드 분석 | 시간에 따른 시장 변화 추적 및 시각화 | P1 |
| 채용공고 통계 | 다양한 차원의 실시간 통계 | P1 |
| 맞춤형 추천 | 사용자 프로필 기반 개인화된 채용 추천 | P1 |
| 기업측 지원자 관리 | 기업이 지원자를 관리할 수 있는 인터페이스 | P1 |
| 알림 시스템 | 이벤트 기반 사용자 알림 | P2 |
| 이메일 인증 | 가입 시 이메일 검증 | P2 |

---

## 2. As-Is Analysis

### 2.1 현재 아키텍처

```
[Client] --> [Spring Boot 3.5.6 / Java 21]
                    |
              [Hexagonal Architecture]
              |- Adapter In (Web/Controller)
              |- Application (Use Cases / Services)
              |- Domain (Entities / Value Objects)
              |- Adapter Out (Persistence / JPA)
                    |
              [MySQL] + [Redis] + [GCS]
```

**기술 스택:**
- **Runtime:** Java 21, Spring Boot 3.5.6
- **Database:** MySQL (JPA + QueryDSL)
- **Cache:** Redis (Spring Data Redis)
- **Storage:** Google Cloud Storage (파일 업로드)
- **Authentication:** JWT + OAuth2 (Google, GitHub)
- **API Documentation:** SpringDoc OpenAPI + Spring REST Docs
- **Monitoring:** Spring Actuator + Micrometer + Grafana Cloud APM
- **Code Quality:** Spring Java Format, MapStruct, Lombok
- **Testing:** JUnit 5, Mockito, H2, Testcontainers
- **CI/CD:** GitHub Actions, Docker

### 2.2 현재 도메인 모델

```
BaseEntity (공통 상속)
  |- createdAt (LocalDateTime)
  |- updatedAt (LocalDateTime)
  |- deletedAt (LocalDateTime, nullable) -- softDelete() 메서드로 현재 시각 설정

users (User)
  |- email, name, password, refreshToken, profileImage
  |- role: USER, ADMIN, PREMIUM (enum)
  |- socialType: LOCAL, GITHUB, GOOGLE, KAKAO, LINKEDIN (enum: SocialType)

companies (Company)
  |- name, industryDomain, websiteUrl, location

company_members (CompanyMember)
  |- user -> companies (role: ADMIN, MEMBER, VIEWER / status: ACTIVE, PENDING, INACTIVE)

company_likes (CompanyLike)
  |- user -> company

job_postings (JobPosting)
  |- company, title, contents, expYears
  |- sourceType: DIRECT/CRAWLED (enum)
  |- originUrl
  |- status: OPEN/CLOSED (enum)

job_skills (JobSkill)
  |- jobPosting -> skill (unique constraint)

skills (Skill)
  |- name (대소문자 무관 중복 방지)

user_skills (UserSkill)
  |- user -> skill (unique constraint)

applications (Application)
  |- user -> jobPosting
  |- status: APPLIED/VIEWED/PASSED/REJECTED

bookmarks (Bookmark)
  |- user -> jobPosting

documents
  |- resumes (Resume): user, title, contents, file
  |- portfolios (Portfolio): user, title, contents, file
  |- educations (Education): user, school, major, degree, dates

careers (UserCareer)
  |- user, company, position, dates
```

### 2.3 현재 API 엔드포인트 (구현 완료)

| 도메인 | 엔드포인트 | 메서드 | 상태 |
|--------|-----------|--------|------|
| **Auth** | /api/v1/auth/register | POST | 완료 |
| | /api/v1/auth/login | POST | 완료 |
| | /oauth2/authorization/{provider} | GET | 완료 |
| | /api/v1/logout | POST | 완료 |
| | /api/v1/reissue | POST | 완료 |
| **User** | /api/v1/user | GET | 완료 (프로필 조회) |
| | /api/v1/user | POST | 완료 (추가 정보 입력) |
| | /api/v1/user/profile-image | PATCH | 완료 (프로필 이미지 업로드) |
| **Company** | /api/v1/companies | POST/GET | 완료 |
| | /api/v1/companies/{id} | GET/PUT/DELETE | 완료 |
| **CompanyLike** | /api/v1/company-likes | POST/GET | 완료 |
| | /api/v1/company-likes/{id} | DELETE | 완료 |
| **JobPosting** | /api/v1/job-postings | POST | 완료 |
| | /api/v1/job-postings/{id} | GET/PUT/DELETE | 완료 |
| **Application** | /api/v1/applications | POST/GET | 완료 |
| | /api/v1/applications/{id} | GET/DELETE | 완료 |
| **Bookmark** | /api/v1/bookmarks | POST/GET | 완료 |
| | /api/v1/bookmarks/{id} | GET/DELETE | 완료 |
| **Resume** | /api/v1/resumes | POST/GET | 완료 |
| | /api/v1/resumes/{id} | GET/PUT/DELETE | 완료 |
| **Portfolio** | /api/v1/portfolios | POST/GET | 완료 |
| | /api/v1/portfolios/{id} | GET/PUT/DELETE | 완료 |
| **Education** | /api/v1/educations | POST/GET | 완료 |
| | /api/v1/educations/{id} | GET/PUT/DELETE | 완료 |
| **Career** | /api/v1/user-careers | POST/GET | 완료 |
| | /api/v1/user-careers/{id} | GET/PUT/DELETE | 완료 |
| **Skill** | /api/v1/skills | POST/GET | 완료 |
| | /api/v1/skills/{id} | GET/PUT/DELETE | 완료 |
| **UserSkill** | /api/v1/user-skills | POST/GET | 완료 |
| | /api/v1/user-skills/{id} | DELETE | 완료 |
| **Mock** | /api/v1/mock/signup | POST | 완료 (테스트용 Mock 사용자 생성) |
| **FileTest** | /api/v1/test/files | - | 유틸리티 전용 (파일 업로드 테스트) |

### 2.4 아키텍처 장점

1. **헥사고날 아키텍처:** 도메인 로직이 인프라에서 분리되어 있어 새로운 어댑터(Elasticsearch, Kafka 등) 추가가 용이
2. **Use Case 기반 설계:** 각 비즈니스 동작이 독립적인 Use Case 인터페이스로 분리 (예: `CreateJobPostingUseCase`, `GetJobPostingUseCase`)
3. **Port/Adapter 패턴:** `LoadJobPostingPort`, `SaveJobPostingPort` 등 인바운드/아웃바운드 포트가 명확히 분리
4. **BaseEntity:** `createdAt`, `updatedAt`, `deletedAt` (LocalDateTime) 공통 필드가 상속 구조로 관리, `softDelete()` 메서드로 삭제 시각 기록
5. **Soft Delete 지원:** `deletedAt` 필드 기반 논리 삭제로 데이터 보존 정책에 부합

---

## 3. Gap Analysis

### 3.1 Critical Gaps (사용자 경험 차단)

| Gap ID | 현재 상태 | 목표 상태 | 영향도 | 우선순위 |
|--------|----------|----------|--------|---------|
| GAP-001 | 채용공고 단건 조회만 가능 | 목록 조회, 검색, 필터링 | **차단적** - 사용자가 공고를 탐색할 수 없음 | P0 |
| GAP-002 | `SourceType.CRAWLED` enum만 존재 | 크롤링 시스템 전체 구현 | **차단적** - 공고 데이터가 수동 입력에만 의존 | P0 |
| GAP-003 | 추천 기능 없음 | AI 기반 맞춤형 추천 | **높음** - 핵심 차별화 가치 | P1 |
| GAP-004 | 통계/분석 기능 없음 | 트렌드 분석 및 통계 대시보드 | **높음** - 데이터 기반 의사결정 불가 | P1 |

### 3.2 Functional Gaps

| Gap ID | 현재 상태 | 목표 상태 | 영향도 | 우선순위 |
|--------|----------|----------|--------|---------|
| GAP-005 | 기업이 지원자를 조회/관리할 수 없음 | 기업측 지원자 관리 대시보드 | 중간 | P1 |
| GAP-006 | 전문 검색 없음 (DB LIKE만 가능) | Elasticsearch 기반 전문 검색 | 중간 | P1 |
| GAP-007 | 알림 기능 없음 | 이벤트 기반 알림 (인앱, 이메일, 푸시) | 중간 | P2 |
| GAP-008 | 이메일 인증 없음 | 가입 시 이메일 검증 | 낮음 | P2 |

### 3.3 Non-Functional Gaps

| Gap ID | 현재 상태 | 목표 상태 |
|--------|----------|----------|
| GAP-NF-001 | 단일 MySQL 쿼리 | Elasticsearch 인덱싱 + 캐시 레이어 |
| GAP-NF-002 | 배치 처리 없음 | Spring Batch 기반 크롤링/분석 파이프라인 |
| GAP-NF-003 | 기본 모니터링 (Grafana APM) | 크롤링/추천 전용 메트릭 + 알림 |
| GAP-NF-004 | PIPA 미준수 | 동의 관리, 데이터 보존, 익명화 |

---

## 4. Functional Requirements

### FR-1: 채용공고 크롤링 시스템

#### FR-1.1 크롤러 엔진 기반 인프라

| 항목 | 내용 |
|------|------|
| **ID** | FR-1.1 |
| **설명** | 외부 채용 사이트(사람인, 잡코리아, 원티드)에서 채용공고를 자동 수집하는 데이터 수집 엔진을 구축한다. **API-First 전략을 적용하여** 가능한 한 공식 REST API를 우선 사용하고, API가 불가능한 경우에만 크롤링으로 대체한다. 각 사이트별 어댑터를 통해 소스 변경에 독립적으로 대응할 수 있어야 한다. 모듈은 모놀리스 내부에 `api/crawling/` 패키지로 포함되며 헥사고날 아키텍처를 따른다. |
| **데이터 수집 전략** | |

| 사이트 | 전략 | 도구 | 비고 |
|--------|------|------|------|
| 사람인 | REST API | Spring WebClient (oapi.saramin.co.kr) | API 키 신청 필요 (api@saramin.co.kr) |
| 원티드 | REST API ONLY | Spring WebClient (openapi.wanted.jobs) | 크롤링 금지 (ToS), API 키 신청 3영업일 소요 |
| 잡코리아 | Hybrid (API 우선, Jsoup 폴백) | Spring WebClient + Jsoup | API 신청 (jobkorea.co.kr/service/api), 거절/제한 시 Jsoup |

| **우선순위** | P0 |
| **복잡도** | XL |
| **인수 조건** | 1) 3개 사이트(사람인, 잡코리아, 원티드) 어댑터가 각각 독립 실행 가능<br>2) 소스 변경 시 해당 사이트 어댑터만 수정하면 됨<br>3) 수집 결과가 raw_crawled_data 테이블에 원본 저장됨<br>4) 정규화된 데이터가 job_postings 테이블에 sourceType=CRAWLED로 저장됨 |
| **어댑터 구조** | `api/crawling/adapter/out/saramin/SaraminApiAdapter.java` (REST API)<br>`api/crawling/adapter/out/wanted/WantedApiAdapter.java` (REST API)<br>`api/crawling/adapter/out/jobkorea/JobKoreaHybridAdapter.java` (API + Jsoup 폴백) |
| **선행 작업** | 1) 사람인 API 키 신청 (api@saramin.co.kr)<br>2) 원티드 API 키 신청 (openapi.wanted.jobs, 3영업일)<br>3) 잡코리아 API 신청 (jobkorea.co.kr/service/api) |
| **의존성** | 없음 (신규 모듈, 모놀리스 내 `api/crawling/` 패키지) |

#### FR-1.2 스케줄링 및 크롤링 전략

| 항목 | 내용 |
|------|------|
| **ID** | FR-1.2 |
| **설명** | 크롤링 주기를 데이터 중요도에 따라 3단계(Hot/Warm/Cold)로 분류하여 스케줄링한다. Hot(인기 공고/최신 공고): 6시간, Warm(일반 공고): 24시간, Cold(오래된 공고): 7일. Spring Batch의 Job/Step으로 관리하며, 실패 시 자동 재시도 및 알림을 제공한다. |
| **우선순위** | P0 |
| **복잡도** | L |
| **인수 조건** | 1) 3단계 스케줄링이 cron expression 기반으로 동작<br>2) 크롤링 실패 시 최대 3회 재시도 후 관리자 알림<br>3) 크롤링 이력(시작/종료 시간, 수집 건수, 오류 건수)이 기록됨<br>4) 관리자가 수동으로 특정 사이트 크롤링을 트리거할 수 있음 |
| **의존성** | FR-1.1 |

#### FR-1.3 중복 제거 및 데이터 정규화

| 항목 | 내용 |
|------|------|
| **ID** | FR-1.3 |
| **설명** | 크롤링된 데이터의 중복을 감지하고 제거한다. URL 기반 1차 중복 체크, 제목+기업명 기반 2차 유사도 체크를 수행한다. 원본 HTML/JSON에서 구조화된 JobPosting 엔티티로 정규화(제목, 기업, 스킬, 경력, 급여, 위치 추출)한다. |
| **우선순위** | P0 |
| **복잡도** | L |
| **인수 조건** | 1) 동일 URL 공고가 중복 저장되지 않음<br>2) 제목+기업명 유사도 90% 이상인 공고는 중복으로 처리됨<br>3) 크롤링 원본 대비 정규화 성공률 95% 이상<br>4) 스킬 추출 시 기존 Skill 테이블과 매칭 및 신규 스킬 자동 등록 |
| **의존성** | FR-1.1, 기존 Skill 도메인 |

#### FR-1.4 크롤링 모니터링 및 관리자 대시보드

| 항목 | 내용 |
|------|------|
| **ID** | FR-1.4 |
| **설명** | 크롤링 시스템의 상태를 실시간으로 모니터링하고 관리할 수 있는 관리자 API를 제공한다. 각 크롤러의 상태(실행 중/대기/오류), 최근 실행 결과, 수집 통계를 조회할 수 있다. |
| **우선순위** | P1 |
| **복잡도** | M |
| **인수 조건** | 1) 각 사이트별 크롤러 상태 조회 API<br>2) 크롤링 이력 목록 조회 (페이징, 기간 필터)<br>3) 수동 크롤링 트리거 API (관리자 전용)<br>4) 크롤링 오류율이 임계값(10%) 초과 시 Grafana 알림 |
| **의존성** | FR-1.1, FR-1.2 |

#### FR-1.5 법적 준수 및 Rate Limiting

| 항목 | 내용 |
|------|------|
| **ID** | FR-1.5 |
| **설명** | 크롤링 시 대상 사이트의 robots.txt를 준수하고, 요청 간 적절한 지연(rate limiting)을 적용한다. 관련 판례에 따라 공개 데이터 수집은 일반적으로 허용되나, 특정 경쟁사 데이터베이스의 실질적 부분을 복제하는 것은 금지된다. 원티드는 ToS에서 크롤링을 금지하므로 반드시 공식 API만 사용한다. |
| **우선순위** | P0 |
| **복잡도** | S |
| **인수 조건** | 1) 크롤링 전 robots.txt 파싱 및 허용 경로만 접근<br>2) 사이트별 요청 간격 최소 2초 이상<br>3) User-Agent에 봇 식별 정보 포함<br>4) 단일 소스에서 전체 데이터의 30% 이상 복제하지 않도록 비율 모니터링 |
| **의존성** | FR-1.1 |

---

### FR-2: 채용 시장 트렌드 분석

#### FR-2.1 시계열 데이터 수집 파이프라인

| 항목 | 내용 |
|------|------|
| **ID** | FR-2.1 |
| **설명** | 크롤링된 채용공고 데이터를 기반으로 일별/주별/월별 시계열 스냅샷을 생성한다. 공고 수, 스킬별 수요, 기업별 채용 규모, 지역별 분포 등의 차원으로 집계한다. |
| **우선순위** | P1 |
| **복잡도** | L |
| **인수 조건** | 1) 일별 배치로 시계열 스냅샷이 자동 생성됨<br>2) 최소 6개월 이상의 히스토리 데이터 유지<br>3) 기업, 산업, 지역, 스킬 4가지 차원으로 집계 가능<br>4) 배치 처리 시간 30분 이내 (10만 건 기준) |
| **의존성** | FR-1 (크롤링 데이터) |

#### FR-2.2 스킬 수요 트렌드 분석

| 항목 | 내용 |
|------|------|
| **ID** | FR-2.2 |
| **설명** | 시간에 따른 기술 스킬의 수요 변화를 추적한다. 급부상 스킬(emerging), 안정 스킬(stable), 하락 스킬(declining)을 분류하고, 스킬 간 동시 출현 빈도(co-occurrence)를 분석하여 스킬 관계를 도출한다. |
| **우선순위** | P1 |
| **복잡도** | L |
| **인수 조건** | 1) 주별 스킬 수요 변화율 계산<br>2) 3개월 이동평균 대비 20% 이상 증가 스킬을 "급부상"으로 분류<br>3) 스킬 간 동시 출현 빈도 상위 10개 조합 제공<br>4) API 응답에 변화 추세 그래프용 데이터 포함 |
| **의존성** | FR-2.1, 기존 Skill 도메인 |

#### FR-2.3 기업/산업별 채용 동향

| 항목 | 내용 |
|------|------|
| **ID** | FR-2.3 |
| **설명** | 기업별 채용 공고 수 증감, 산업별 채용 추이를 추적한다. 특정 기업의 채용 확대/축소 신호를 감지하고, 산업 전반의 채용 활성도를 지수화한다. |
| **우선순위** | P1 |
| **복잡도** | M |
| **인수 조건** | 1) 기업별 월별 공고 수 추이 API<br>2) 전월 대비 30% 이상 증감 시 "주목할 변화"로 플래그<br>3) 산업별 채용 활성도 지수 (0-100) 제공<br>4) 최소 Top 50 기업, Top 10 산업 커버 |
| **의존성** | FR-2.1, 기존 Company 도메인 |

#### FR-2.4 급여 트렌드 분석

| 항목 | 내용 |
|------|------|
| **ID** | FR-2.4 |
| **설명** | 크롤링된 공고에서 급여 정보를 추출하고, 직군/경력/지역별 급여 범위 및 추이를 분석한다. 급여 정보가 명시되지 않은 공고는 유사 조건 기반으로 추정 범위를 제공한다. |
| **우선순위** | P2 |
| **복잡도** | XL |
| **인수 조건** | 1) 급여 정보 추출 정확도 80% 이상 (명시된 경우)<br>2) 직군/경력/지역 조합별 급여 중위값 및 IQR 제공<br>3) 분기별 급여 변화율 추적<br>4) 급여 미명시 공고에 대한 추정 범위 제공 (신뢰구간 포함) |
| **의존성** | FR-2.1, FR-1.3 |

---

### FR-3: 채용공고 통계 대시보드

#### FR-3.1 실시간 채용 현황 통계

| 항목 | 내용 |
|------|------|
| **ID** | FR-3.1 |
| **설명** | 현재 활성화된 채용공고의 다양한 차원별 통계를 실시간으로 제공한다. 전체 활성 공고 수, 오늘 신규 공고 수, 마감 임박 공고 수 등을 포함한다. |
| **우선순위** | P1 |
| **복잡도** | M |
| **인수 조건** | 1) 전체 활성 공고 수를 실시간 반환 (캐시 TTL 5분)<br>2) 일별 신규/마감 공고 수 제공<br>3) 소스별(DIRECT/CRAWLED) 공고 비율 제공<br>4) API 응답 시간 200ms 이내 (p95) |
| **의존성** | 기존 JobPosting 도메인 |

#### FR-3.2 기업별 채용 통계

| 항목 | 내용 |
|------|------|
| **ID** | FR-3.2 |
| **설명** | 기업별 활성 공고 수, 평균 경력 요구사항, 주요 모집 스킬을 집계한다. 기업 프로필 페이지에 표시할 수 있는 형태로 제공한다. |
| **우선순위** | P1 |
| **복잡도** | S |
| **인수 조건** | 1) 기업별 활성 공고 수 집계<br>2) 기업별 Top 5 요구 스킬 목록<br>3) 기업별 평균/최소/최대 요구 경력<br>4) 결과 캐시 (TTL 1시간) |
| **의존성** | 기존 Company, JobPosting, JobSkill 도메인 |

#### FR-3.3 산업/지역별 통계

| 항목 | 내용 |
|------|------|
| **ID** | FR-3.3 |
| **설명** | 산업 도메인별, 지역별 채용 현황을 집계한다. 히트맵 또는 차트 시각화에 사용할 수 있는 형태의 API를 제공한다. |
| **우선순위** | P1 |
| **복잡도** | M |
| **인수 조건** | 1) Company.industryDomain 기준 산업별 공고 수<br>2) Company.location 기준 지역별 공고 수<br>3) 산업x지역 교차 통계 제공<br>4) 결과 캐시 (TTL 1시간) |
| **의존성** | 기존 Company, JobPosting 도메인 |

#### FR-3.4 스킬별 채용 수요 통계

| 항목 | 내용 |
|------|------|
| **ID** | FR-3.4 |
| **설명** | 각 스킬별 관련 채용공고 수, 스킬 수요 순위를 제공한다. 전체 순위 및 산업별/지역별 세분화된 순위를 지원한다. |
| **우선순위** | P1 |
| **복잡도** | M |
| **인수 조건** | 1) 전체 스킬 수요 순위 Top 50<br>2) 산업별/지역별 필터링 가능<br>3) 스킬별 공고 수 및 전체 대비 비율<br>4) 결과 캐시 (TTL 30분) |
| **의존성** | 기존 Skill, JobSkill 도메인 |

---

### FR-4: 맞춤형 채용 추천 시스템

#### FR-4.1 콘텐츠 기반 필터링 (Content-Based Filtering)

| 항목 | 내용 |
|------|------|
| **ID** | FR-4.1 |
| **설명** | 사용자 프로필(UserSkill, UserCareer, Education)과 채용공고(JobSkill, expYears, location)의 유사도를 계산하여 매칭한다. TF-IDF 벡터화 후 코사인 유사도를 사용하며, 스킬 매칭(60%), 경력 매칭(25%), 지역 매칭(15%)의 가중치를 적용한다. |
| **우선순위** | P1 |
| **복잡도** | XL |
| **인수 조건** | 1) 사용자 스킬과 공고 스킬의 매칭 점수 계산<br>2) 경력 적합도 계산 (expYears vs UserCareer 합산 기간)<br>3) 추천 결과 Top 20을 500ms 이내 반환 (p95)<br>4) 추천 정확도: 추천 공고 중 사용자 클릭/지원 비율 15% 이상 |
| **의존성** | 기존 UserSkill, UserCareer, JobSkill, JobPosting 도메인 |

#### FR-4.2 협업 필터링 (Collaborative Filtering)

| 항목 | 내용 |
|------|------|
| **ID** | FR-4.2 |
| **설명** | "이 공고에 지원한 사용자들이 함께 지원한 공고" 패턴을 분석하여 추천한다. Application 데이터를 기반으로 사용자-공고 상호작용 행렬을 구성하고, 아이템 기반 협업 필터링을 적용한다. |
| **우선순위** | P1 |
| **복잡도** | XL |
| **인수 조건** | 1) 사용자-공고 상호작용 행렬 기반 유사 공고 계산<br>2) 최소 100명 이상 활성 사용자 데이터로 동작<br>3) 일별 배치로 추천 모델 재학습<br>4) 콘텐츠 기반 대비 다양성(diversity) 20% 이상 향상 |
| **의존성** | FR-4.1, 기존 Application 도메인 |

#### FR-4.3 하이브리드 추천 및 랭킹

| 항목 | 내용 |
|------|------|
| **ID** | FR-4.3 |
| **설명** | 콘텐츠 기반과 협업 필터링의 결과를 가중 합산하여 최종 추천 목록을 생성한다. 사용자 행동 데이터가 충분한 경우 협업 필터링 비중을 높이고, 부족한 경우 콘텐츠 기반 비중을 높이는 적응형 가중치를 적용한다. |
| **우선순위** | P1 |
| **복잡도** | L |
| **인수 조건** | 1) 콘텐츠:협업 비율이 사용자 활동량에 따라 동적 조정<br>2) 추천 목록에 중복 없음<br>3) 추천 이유(reason) 문자열 각 항목에 포함<br>4) A/B 테스트 프레임워크 내장 (가중치 실험 가능) |
| **의존성** | FR-4.1, FR-4.2 |

#### FR-4.4 Cold Start 해결

| 항목 | 내용 |
|------|------|
| **ID** | FR-4.4 |
| **설명** | 신규 사용자(프로필 데이터 없음) 및 신규 공고(상호작용 데이터 없음)의 cold start 문제를 해결한다. 신규 사용자에게는 온보딩 질문(희망 직군, 주요 스킬 3개, 경력 수준, 희망 지역)을 통해 초기 프로필을 구성하고, 이후 인기도 기반 추천으로 보완한다. |
| **우선순위** | P1 |
| **복잡도** | M |
| **인수 조건** | 1) 온보딩 질문 API (4문항, 각 선택형)<br>2) 온보딩 미완료 사용자에게 인기도 기반 추천 제공<br>3) 온보딩 완료 시 즉시 콘텐츠 기반 추천 생성<br>4) 신규 공고는 카테고리/스킬 기반으로 유사 공고 클러스터에 배치 |
| **의존성** | FR-4.1 |

#### FR-4.5 추천 피드백 및 학습

| 항목 | 내용 |
|------|------|
| **ID** | FR-4.5 |
| **설명** | 사용자의 추천 반응(클릭, 북마크, 지원, 무시, "관심 없음")을 수집하여 추천 모델의 개인화 정확도를 지속적으로 개선한다. |
| **우선순위** | P2 |
| **복잡도** | M |
| **인수 조건** | 1) 추천 항목별 사용자 반응 이벤트 수집<br>2) "관심 없음" 피드백 시 해당 유형 공고 비중 감소<br>3) 피드백 반영 후 추천 품질 개선 측정 가능<br>4) 피드백 데이터 PIPA 준수 (익명화 가능) |
| **의존성** | FR-4.3 |

---

### FR-5: 채용공고 검색 및 필터링

#### FR-5.1 전문 검색 엔진

| 항목 | 내용 |
|------|------|
| **ID** | FR-5.1 |
| **설명** | Elasticsearch를 활용하여 채용공고의 제목, 내용, 스킬에 대한 전문 검색(full-text search)을 제공한다. 한국어 형태소 분석(nori), 자동완성(suggest), 오타 보정(fuzzy)을 지원한다. |
| **우선순위** | P0 |
| **복잡도** | XL |
| **인수 조건** | 1) 한국어 검색 시 형태소 분석 적용 (nori tokenizer)<br>2) 검색 결과 200ms 이내 반환 (p95)<br>3) 자동완성 100ms 이내 반환 (p95)<br>4) 오타 1글자까지 보정 (fuzzy distance 1) |
| **의존성** | 기존 JobPosting 도메인, Elasticsearch 인프라 |

#### FR-5.2 다차원 필터링

| 항목 | 내용 |
|------|------|
| **ID** | FR-5.2 |
| **설명** | 검색 결과를 다양한 차원으로 필터링한다: 스킬, 경력(연차), 지역, 산업, 기업, 소스 타입(DIRECT/CRAWLED), 공고 상태(OPEN/CLOSED), 등록일 범위. 복합 필터를 AND 조건으로 결합할 수 있다. |
| **우선순위** | P0 |
| **복잡도** | L |
| **인수 조건** | 1) 8가지 필터 조건 모두 동작<br>2) 복합 필터 3개 이상 조합 시에도 200ms 이내<br>3) 각 필터별 해당 결과 건수(facet count) 제공<br>4) 빈 결과 시 유사 검색어 제안 |
| **의존성** | FR-5.1 |

#### FR-5.3 채용공고 목록 조회 (페이징/정렬)

| 항목 | 내용 |
|------|------|
| **ID** | FR-5.3 |
| **설명** | 검색어 없이 채용공고 전체 목록을 페이징하여 조회한다. 정렬 기준: 최신순, 관련도순, 인기순(지원 수), 마감 임박순. Cursor-based pagination을 사용하여 대용량 데이터에서도 일관된 성능을 보장한다. |
| **우선순위** | P0 |
| **복잡도** | M |
| **인수 조건** | 1) 커서 기반 페이징 (페이지당 20건 기본, 최대 50건)<br>2) 4가지 정렬 기준 모두 동작<br>3) 10만 건 데이터에서 어떤 페이지든 200ms 이내<br>4) 총 결과 건수(totalCount) 헤더에 포함 |
| **의존성** | FR-5.1, 기존 JobPosting 도메인 |

---

### FR-6: 기업측 지원자 관리

#### FR-6.1 지원자 목록 조회

| 항목 | 내용 |
|------|------|
| **ID** | FR-6.1 |
| **설명** | 기업 관리자(CompanyMember.role=ADMIN)가 자사 채용공고에 대한 지원자 목록을 조회할 수 있다. 공고별, 전체 지원자별로 조회 가능하며, 지원 상태(APPLIED/VIEWED/PASSED/REJECTED)별 필터링을 지원한다. |
| **우선순위** | P1 |
| **복잡도** | M |
| **인수 조건** | 1) 공고별 지원자 목록 페이징 조회<br>2) 지원 상태별 필터링<br>3) 지원자 이름/이메일 검색<br>4) CompanyMember.role=ADMIN 권한 검증 |
| **의존성** | 기존 Application, CompanyMember 도메인 |

#### FR-6.2 지원 상태 관리

| 항목 | 내용 |
|------|------|
| **ID** | FR-6.2 |
| **설명** | 기업 관리자가 지원자의 상태를 변경(APPLIED -> VIEWED -> PASSED/REJECTED)할 수 있다. 상태 변경 시 지원자에게 알림이 발송된다. 벌크 상태 변경(다수 지원자 일괄 처리)을 지원한다. |
| **우선순위** | P1 |
| **복잡도** | M |
| **인수 조건** | 1) 단건 상태 변경 API<br>2) 벌크 상태 변경 API (최대 50건)<br>3) 상태 변경 이력 저장<br>4) 상태 변경 시 알림 이벤트 발행 (FR-7 연동) |
| **의존성** | FR-6.1, FR-7 (알림) |

#### FR-6.3 지원자 이력서/포트폴리오 열람

| 항목 | 내용 |
|------|------|
| **ID** | FR-6.3 |
| **설명** | 기업 관리자가 지원자의 이력서, 포트폴리오, 학력, 경력 정보를 열람할 수 있다. 열람 시 지원 상태가 자동으로 VIEWED로 변경된다. |
| **우선순위** | P1 |
| **복잡도** | S |
| **인수 조건** | 1) 지원자 프로필 상세 조회 API (이력서, 포트폴리오, 학력, 경력, 스킬 통합)<br>2) 최초 열람 시 자동 VIEWED 상태 전환<br>3) 열람 로그 기록 (누가, 언제, 어떤 지원자를)<br>4) 지원자 동의 없는 정보는 마스킹 처리 |
| **의존성** | FR-6.1, 기존 Document/Career/Skill 도메인 |

---

### FR-7: 알림 시스템

#### FR-7.1 이벤트 기반 알림 인프라

| 항목 | 내용 |
|------|------|
| **ID** | FR-7.1 |
| **설명** | Spring Event 기반의 비동기 알림 시스템을 구축한다. 알림 타입별(인앱, 이메일) 채널을 지원하며, 알림 큐를 통해 대량 알림 발송 시에도 메인 트랜잭션에 영향을 주지 않는다. |
| **우선순위** | P2 |
| **복잡도** | L |
| **인수 조건** | 1) 알림 생성이 메인 트랜잭션과 비동기 분리<br>2) 인앱 알림 저장 및 조회 API<br>3) 이메일 알림 발송 (SMTP 또는 외부 서비스)<br>4) 알림 발송 실패 시 최대 3회 재시도 |
| **의존성** | 없음 (신규 모듈) |

#### FR-7.2 알림 이벤트 정의

| 항목 | 내용 |
|------|------|
| **ID** | FR-7.2 |
| **설명** | 다음 이벤트에 대해 알림을 발생시킨다: 1) 새 지원 접수 (기업에게), 2) 지원 상태 변경 (지원자에게), 3) 새로운 매칭 공고 (추천 기반), 4) 북마크한 공고 마감 임박, 5) 프로필 미완성 리마인더. |
| **우선순위** | P2 |
| **복잡도** | M |
| **인수 조건** | 1) 5가지 이벤트 타입 모두 알림 발생<br>2) 사용자별 알림 설정(on/off) 지원<br>3) 알림 읽음/안읽음 상태 관리<br>4) 안읽은 알림 건수 API (배지용) |
| **의존성** | FR-7.1, FR-4 (추천), FR-6 (지원 상태) |

---

### FR-8: 이메일 인증

#### FR-8.1 가입 시 이메일 인증

| 항목 | 내용 |
|------|------|
| **ID** | FR-8.1 |
| **설명** | 일반 회원가입(non-OAuth2) 시 이메일 인증 코드를 발송하고, 인증 완료 전까지 계정을 비활성 상태로 유지한다. 인증 코드는 6자리 숫자, 유효기간 10분, Redis에 저장한다. |
| **우선순위** | P2 |
| **복잡도** | M |
| **인수 조건** | 1) 가입 시 인증 코드 이메일 발송<br>2) 인증 코드 6자리, 유효기간 10분<br>3) 3회 이상 잘못된 코드 입력 시 재발송 필요<br>4) OAuth2 가입 사용자는 이메일 인증 면제<br>5) 인증 미완료 계정 24시간 후 자동 삭제 |
| **의존성** | 기존 User 도메인, Redis |

#### FR-8.2 이메일 변경 인증

| 항목 | 내용 |
|------|------|
| **ID** | FR-8.2 |
| **설명** | 기존 사용자가 이메일을 변경할 때 새 이메일로 인증 코드를 발송하고, 인증 완료 후 이메일을 변경한다. |
| **우선순위** | P2 |
| **복잡도** | S |
| **인수 조건** | 1) 새 이메일로 인증 코드 발송<br>2) 인증 완료 후 이메일 업데이트<br>3) 기존 이메일과 동일한 경우 거부<br>4) 이미 사용 중인 이메일 거부 |
| **의존성** | FR-8.1 |

---

## 5. Non-Functional Requirements

### NFR-1: 성능 (Performance)

| ID | 요구사항 | 측정 목표 | 모니터링 방법 | Fallback 전략 |
|----|---------|----------|-------------|--------------|
| NFR-1.1 | 채용공고 검색 응답 시간 | p50 < 100ms, p95 < 200ms, p99 < 500ms | Grafana APM + Micrometer Timer | Elasticsearch 장애 시 MySQL LIKE 쿼리 대체 (성능 저하 허용) |
| NFR-1.2 | 추천 결과 응답 시간 | p50 < 200ms, p95 < 500ms, p99 < 1000ms | Grafana APM + Custom Metric | 캐시된 추천 목록 반환 (실시간 계산 대신) |
| NFR-1.3 | API 전반 응답 시간 | p95 < 300ms (검색/추천 제외) | Spring Actuator + Micrometer | Circuit breaker (Resilience4j) |
| NFR-1.4 | 크롤링 처리량 | 사이트당 1000건/시간 이상 | 크롤링 배치 메트릭 | 병렬 워커 수 동적 조정 |
| NFR-1.5 | 데이터베이스 쿼리 | 단일 쿼리 p95 < 50ms | MySQL slow query log + APM | 쿼리 플랜 자동 분석 알림 |

### NFR-2: 확장성 (Scalability)

| ID | 요구사항 | 측정 목표 | 모니터링 방법 | Fallback 전략 |
|----|---------|----------|-------------|--------------|
| NFR-2.1 | 동시 사용자 | 10,000명 동시 접속 | Load test (k6/Gatling) 월 1회 | 오토스케일링 (HPA) 트리거 |
| NFR-2.2 | 채용공고 데이터 규모 | 100만 건 이상 | DB 테이블 사이즈 모니터링 | 파티셔닝 (등록일 기준) |
| NFR-2.3 | 검색 인덱스 규모 | 100만 문서, 50GB 이하 | Elasticsearch 클러스터 메트릭 | 샤드 재분배, 콜드 인덱스 분리 |
| NFR-2.4 | 크롤링 수평 확장 | 크롤러 인스턴스 3개 이상 | 배치 작업 모니터링 | 크롤링 대상 파티셔닝 |

### NFR-3: 보안 (Security)

| ID | 요구사항 | 측정 목표 | 모니터링 방법 | Fallback 전략 |
|----|---------|----------|-------------|--------------|
| NFR-3.1 | 인증/인가 | 모든 API 인증 필수 (공개 API 제외) | Spring Security 감사 로그 | JWT 블랙리스트 (Redis) |
| NFR-3.2 | 데이터 암호화 | 전송: TLS 1.3, 저장: AES-256 (PII) | Certificate 만료 모니터링 | 암호화 키 로테이션 |
| NFR-3.3 | Rate Limiting | API: 100req/min/user, 검색: 30req/min | Redis 기반 Rate Limiter 메트릭 | 트래픽 급증 시 자동 차단 |
| NFR-3.4 | 취약점 관리 | OWASP Top 10 대응 | 의존성 보안 스캔 (Dependabot) | WAF 룰 즉시 배포 |
| NFR-3.5 | 크롤링 보안 | 크롤링 서버 IP 외부 노출 최소화 | Proxy/VPN 상태 모니터링 | 크롤링 IP 로테이션 |

### NFR-4: 가용성 (Availability)

| ID | 요구사항 | 측정 목표 | 모니터링 방법 | Fallback 전략 |
|----|---------|----------|-------------|--------------|
| NFR-4.1 | 서비스 가용성 | 99.5% (월간) | Uptime 모니터링 (Grafana) | Blue-Green 배포 |
| NFR-4.2 | 데이터 내구성 | RPO < 1시간, RTO < 4시간 | 백업 완료 알림 | MySQL 리플리카 자동 승격 |
| NFR-4.3 | Elasticsearch 가용성 | 99.0% | 클러스터 헬스 모니터링 | MySQL 직접 쿼리 대체 모드 |
| NFR-4.4 | 크롤링 시스템 가용성 | 95.0% (크롤러 자체) | 배치 실행 성공률 | 실패 크롤링 자동 재스케줄링 |

### NFR-5: 규정 준수 (Compliance)

| ID | 요구사항 | 측정 목표 | 모니터링 방법 | Fallback 전략 |
|----|---------|----------|-------------|--------------|
| NFR-5.1 | PIPA (개인정보보호법) 준수 | 100% 준수 | 분기별 컴플라이언스 감사 | 법무팀 즉시 자문 |
| NFR-5.2 | 데이터 수집 동의 | 모든 PII 수집 시 명시적 동의 | 동의 이력 DB 저장 | 동의 미확인 데이터 접근 차단 |
| NFR-5.3 | 데이터 보존 기간 | 비활성 3년 후 자동 삭제/익명화 | 월별 배치 스캔 | 수동 삭제 프로세스 |
| NFR-5.4 | AI 설명 의무 | 추천 결과에 대한 설명 제공 | 추천 API reason 필드 제공률 | 추천 비활성화 (수동 검색만) |
| NFR-5.5 | 크롤링 법적 준수 | robots.txt 준수, ToS 위반 없음 | 크롤링 대상 사이트 정책 월별 검토 | 위반 감지 시 해당 사이트 크롤링 즉시 중단 |

---

## 6. Data Architecture

### 6.1 신규 테이블 설계

#### 6.1.1 크롤링 관련 테이블

```sql
-- 크롤링 원본 데이터 저장
CREATE TABLE raw_crawled_data (
    raw_data_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_site     VARCHAR(50)  NOT NULL COMMENT '크롤링 소스 사이트 (SARAMIN, JOBKOREA, WANTED)',
    source_url      VARCHAR(2083) NOT NULL UNIQUE COMMENT '원본 URL',
    raw_content     MEDIUMTEXT   NOT NULL COMMENT '원본 HTML/JSON',
    content_hash    VARCHAR(64)  NOT NULL COMMENT 'SHA-256 해시 (변경 감지용)',
    crawled_at      DATETIME(6)  NOT NULL,
    processed       BOOLEAN      DEFAULT FALSE COMMENT '정규화 처리 완료 여부',
    processing_error TEXT        NULL COMMENT '정규화 실패 시 오류 메시지',
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_source_site (source_site),
    INDEX idx_processed (processed),
    INDEX idx_crawled_at (crawled_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 크롤링 실행 이력
CREATE TABLE crawl_executions (
    execution_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_site     VARCHAR(50)  NOT NULL,
    started_at      DATETIME(6)  NOT NULL,
    finished_at     DATETIME(6)  NULL,
    status          VARCHAR(20)  NOT NULL COMMENT 'RUNNING, COMPLETED, FAILED, CANCELLED',
    total_crawled   INT          DEFAULT 0,
    total_new       INT          DEFAULT 0,
    total_updated   INT          DEFAULT 0,
    total_duplicate INT          DEFAULT 0,
    total_error     INT          DEFAULT 0,
    error_message   TEXT         NULL,
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_source_site_status (source_site, status),
    INDEX idx_started_at (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 6.1.2 JobPosting 확장 필드

```sql
-- 기존 job_postings 테이블에 추가할 컬럼
ALTER TABLE job_postings ADD COLUMN salary_min       INT          NULL COMMENT '최소 연봉 (만원)';
ALTER TABLE job_postings ADD COLUMN salary_max       INT          NULL COMMENT '최대 연봉 (만원)';
ALTER TABLE job_postings ADD COLUMN location         VARCHAR(200) NULL COMMENT '근무지';
ALTER TABLE job_postings ADD COLUMN deadline_at      DATETIME(6)  NULL COMMENT '공고 마감일';
ALTER TABLE job_postings ADD COLUMN view_count       INT          DEFAULT 0 COMMENT '조회수';
ALTER TABLE job_postings ADD COLUMN application_count INT         DEFAULT 0 COMMENT '지원수 (비정규화)';
ALTER TABLE job_postings ADD COLUMN raw_data_id      BIGINT       NULL COMMENT 'FK to raw_crawled_data';
ALTER TABLE job_postings ADD COLUMN last_crawled_at  DATETIME(6)  NULL COMMENT '마지막 크롤링 확인 시각';

ALTER TABLE job_postings ADD INDEX idx_status_deadline (status, deadline_at);
ALTER TABLE job_postings ADD INDEX idx_source_type (source_type);
ALTER TABLE job_postings ADD INDEX idx_location (location);
ALTER TABLE job_postings ADD INDEX idx_salary (salary_min, salary_max);
```

#### 6.1.3 트렌드/통계 관련 테이블

```sql
-- 일별 시계열 스냅샷
CREATE TABLE daily_statistics (
    stat_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date       DATE         NOT NULL,
    dimension_type  VARCHAR(30)  NOT NULL COMMENT 'SKILL, COMPANY, INDUSTRY, LOCATION',
    dimension_value VARCHAR(200) NOT NULL COMMENT '차원 값 (스킬명, 기업명 등)',
    active_count    INT          NOT NULL DEFAULT 0 COMMENT '활성 공고 수',
    new_count       INT          NOT NULL DEFAULT 0 COMMENT '신규 공고 수',
    closed_count    INT          NOT NULL DEFAULT 0 COMMENT '마감 공고 수',
    avg_salary_min  INT          NULL COMMENT '평균 최소 연봉',
    avg_salary_max  INT          NULL COMMENT '평균 최대 연봉',
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_date_dimension (stat_date, dimension_type, dimension_value),
    INDEX idx_dimension_type (dimension_type),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 스킬 동시 출현 빈도
CREATE TABLE skill_cooccurrence (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_id_1      BIGINT       NOT NULL,
    skill_id_2      BIGINT       NOT NULL,
    cooccurrence_count INT       NOT NULL DEFAULT 0,
    last_updated    DATETIME(6)  NOT NULL,
    UNIQUE KEY uk_skill_pair (skill_id_1, skill_id_2),
    FOREIGN KEY (skill_id_1) REFERENCES skills(skill_id),
    FOREIGN KEY (skill_id_2) REFERENCES skills(skill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 6.1.4 추천 시스템 관련 테이블

```sql
-- 사용자 행동 이벤트 (추천 학습용)
CREATE TABLE user_events (
    event_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    jobposting_id   BIGINT       NOT NULL,
    event_type      VARCHAR(20)  NOT NULL COMMENT 'VIEW, CLICK, BOOKMARK, APPLY, DISMISS',
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_user_id (user_id),
    INDEX idx_jobposting_id (jobposting_id),
    INDEX idx_event_type (event_type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (jobposting_id) REFERENCES job_postings(jobposting_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 미리 계산된 추천 결과 캐시
CREATE TABLE recommendation_cache (
    cache_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    jobposting_id   BIGINT       NOT NULL,
    score           DOUBLE       NOT NULL COMMENT '추천 점수 (0.0~1.0)',
    reason          VARCHAR(500) NOT NULL COMMENT '추천 이유',
    strategy        VARCHAR(30)  NOT NULL COMMENT 'CONTENT, COLLABORATIVE, HYBRID, POPULARITY',
    rank_position   INT          NOT NULL,
    generated_at    DATETIME(6)  NOT NULL,
    expires_at      DATETIME(6)  NOT NULL,
    UNIQUE KEY uk_user_job (user_id, jobposting_id),
    INDEX idx_user_rank (user_id, rank_position),
    INDEX idx_expires (expires_at),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (jobposting_id) REFERENCES job_postings(jobposting_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 온보딩 질문 응답
CREATE TABLE user_onboarding (
    onboarding_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL UNIQUE,
    preferred_role  VARCHAR(100) NULL COMMENT '희망 직군',
    experience_level VARCHAR(20) NULL COMMENT 'JUNIOR, MID, SENIOR',
    preferred_location VARCHAR(200) NULL COMMENT '희망 근무지',
    completed       BOOLEAN      DEFAULT FALSE,
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 온보딩 시 선택한 스킬 (별도 테이블: 다대다)
CREATE TABLE onboarding_skills (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    onboarding_id   BIGINT       NOT NULL,
    skill_id        BIGINT       NOT NULL,
    UNIQUE KEY uk_onboarding_skill (onboarding_id, skill_id),
    FOREIGN KEY (onboarding_id) REFERENCES user_onboarding(onboarding_id),
    FOREIGN KEY (skill_id) REFERENCES skills(skill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 6.1.5 알림 관련 테이블

```sql
-- 알림
CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    type            VARCHAR(50)  NOT NULL COMMENT 'NEW_APPLICATION, STATUS_CHANGE, RECOMMENDATION, DEADLINE, REMINDER',
    title           VARCHAR(200) NOT NULL,
    message         TEXT         NOT NULL,
    reference_type  VARCHAR(50)  NULL COMMENT '참조 엔티티 타입 (JOB_POSTING, APPLICATION 등)',
    reference_id    BIGINT       NULL COMMENT '참조 엔티티 ID',
    is_read         BOOLEAN      DEFAULT FALSE,
    channel         VARCHAR(20)  NOT NULL COMMENT 'IN_APP, EMAIL',
    sent_at         DATETIME(6)  NULL,
    read_at         DATETIME(6)  NULL,
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_user_created (user_id, created_at DESC),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 사용자 알림 설정
CREATE TABLE notification_settings (
    setting_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL UNIQUE,
    new_application BOOLEAN      DEFAULT TRUE,
    status_change   BOOLEAN      DEFAULT TRUE,
    recommendation  BOOLEAN      DEFAULT TRUE,
    deadline_alert  BOOLEAN      DEFAULT TRUE,
    profile_reminder BOOLEAN     DEFAULT TRUE,
    email_enabled   BOOLEAN      DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 6.1.6 지원 상태 변경 이력

```sql
-- 지원 상태 변경 이력
CREATE TABLE application_status_history (
    history_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id  BIGINT       NOT NULL,
    previous_status VARCHAR(20)  NOT NULL,
    new_status      VARCHAR(20)  NOT NULL,
    changed_by      BIGINT       NOT NULL COMMENT '변경한 사용자 ID',
    note            TEXT         NULL COMMENT '변경 사유',
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_application_id (application_id),
    FOREIGN KEY (application_id) REFERENCES applications(application_id),
    FOREIGN KEY (changed_by) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 6.1.7 데이터 마이그레이션 계획 (job_postings 8개 신규 컬럼)

`job_postings` 테이블에 8개 컬럼을 추가할 때 무중단 배포를 보장하기 위한 전략:

**원칙:** 모든 신규 컬럼은 `NULL DEFAULT` 또는 명시적 `DEFAULT` 값을 가지며, `NOT NULL` 제약은 데이터 백필 완료 후 별도 마이그레이션으로 추가한다.

| 단계 | 작업 | 무중단 보장 방법 |
|------|------|-----------------|
| 1 | `ALTER TABLE ADD COLUMN` (8개, 모두 NULL 허용) | MySQL Online DDL (`ALGORITHM=INPLACE`) 사용, 테이블 락 최소화 |
| 2 | 애플리케이션 배포 (신규 컬럼 읽기/쓰기 코드) | 기존 컬럼에는 영향 없음, 신규 컬럼 NULL 허용이므로 구버전 코드 호환 |
| 3 | 백필 배치 실행 (기존 데이터에 기본값 채우기) | Spring Batch로 청크 단위 (500건씩) 업데이트, 피크 시간 회피 |
| 4 | NOT NULL 제약 추가 (필요한 컬럼만) | 백필 완료 확인 후 별도 마이그레이션 |

**주의사항:**
- `ALTER TABLE`은 한 번에 하나씩 실행하여 리스크 분산
- 대형 테이블의 경우 `pt-online-schema-change` 도구 사용 고려
- 각 ALTER 후 슬로우 쿼리 로그 확인

### 6.1.8 raw_crawled_data 아카이빙 전략

`raw_crawled_data.raw_content`는 `MEDIUMTEXT` 타입으로 레코드당 수 MB에 달할 수 있어, 장기 보관 시 MySQL 성능에 영향을 준다.

| 항목 | 정책 |
|------|------|
| **핫 데이터** | 30일 이내: MySQL에 전체 데이터 유지 |
| **아카이브** | 30일 경과: `raw_content`를 GCS(Google Cloud Storage)에 JSON 파일로 이관 |
| **MySQL 보존** | 아카이브 후 MySQL에는 메타데이터(source_site, source_url, content_hash, crawled_at)만 유지, `raw_content`는 NULL로 변경 |
| **배치 주기** | 주 1회 (일요일 새벽 3시) Spring Batch 실행 |
| **GCS 경로** | `gs://{bucket}/crawled-archive/{year}/{month}/{source_site}/{raw_data_id}.json` |
| **보존 기간** | GCS에서 1년 보관 후 Nearline Storage로 자동 전환 |

### 6.2 MySQL-to-Elasticsearch 동기화 전략

**결정: Spring ApplicationEvent 기반 동기화**

MySQL과 Elasticsearch 간 데이터 정합성을 유지하기 위해 Spring ApplicationEvent 패턴을 사용한다.

#### 6.2.1 실시간 동기화 흐름

```
JobPostingService.save()
  -> MySQL write (JPA flush)
  -> ApplicationEventPublisher.publishEvent(JobPostingChangedEvent)
  -> JobPostingIndexListener.onJobPostingChanged()  [@TransactionalEventListener(phase = AFTER_COMMIT)]
    -> ElasticsearchRepository.save(JobPostingDocument)
```

| 이벤트 | 트리거 시점 | ES 동작 |
|--------|-----------|---------|
| JobPosting 생성 | `save()` 후 | ES 문서 인덱싱 |
| JobPosting 수정 | `update()` 후 | ES 문서 업데이트 |
| JobPosting 삭제 | `softDelete()` 후 | ES 문서 삭제 |

#### 6.2.2 초기 전체 동기화

- **도구:** Spring Batch Job
- **동작:** MySQL `job_postings` 전체 레코드를 청크 단위(500건)로 읽어 ES에 벌크 인덱싱
- **실행 시점:** ES 인덱스 최초 생성 시, 또는 인덱스 재구축 필요 시 관리자가 수동 트리거

#### 6.2.3 정합성 검증

- **주기:** 주 1회 (일요일 새벽 2시)
- **동작:** MySQL `job_postings` COUNT vs ES 문서 COUNT 비교
- **불일치 시:** 차이가 1% 초과하면 전체 재인덱싱 배치 트리거 + Grafana 알림
- **세부 검증:** 랜덤 샘플 100건의 `updatedAt` 타임스탬프 비교

#### 6.2.4 장애 대응 (ES 다운 시)

- ES 연결 실패 시 이벤트를 Redis List (`es:sync:pending`)에 큐잉
- ES 복구 감지 시 (`@Scheduled` 헬스체크, 30초 간격) 큐에서 이벤트를 꺼내 순차 처리
- Redis 큐 TTL: 24시간 (이후 미처리 이벤트는 정합성 검증 배치에서 처리)

### 6.3 Elasticsearch 인덱스 설계

```json
{
  "job_postings_index": {
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1,
      "analysis": {
        "analyzer": {
          "korean_analyzer": {
            "type": "custom",
            "tokenizer": "nori_tokenizer",
            "filter": ["nori_readingform", "lowercase"]
          },
          "autocomplete_analyzer": {
            "type": "custom",
            "tokenizer": "edge_ngram_tokenizer",
            "filter": ["lowercase"]
          }
        },
        "tokenizer": {
          "nori_tokenizer": {
            "type": "nori_tokenizer",
            "decompound_mode": "mixed"
          },
          "edge_ngram_tokenizer": {
            "type": "edge_ngram",
            "min_gram": 1,
            "max_gram": 20,
            "token_chars": ["letter", "digit"]
          }
        }
      }
    },
    "mappings": {
      "properties": {
        "id":               { "type": "long" },
        "title":            { "type": "text", "analyzer": "korean_analyzer", "fields": { "autocomplete": { "type": "text", "analyzer": "autocomplete_analyzer" }}},
        "contents":         { "type": "text", "analyzer": "korean_analyzer" },
        "companyName":      { "type": "keyword", "fields": { "text": { "type": "text", "analyzer": "korean_analyzer" }}},
        "companyId":        { "type": "long" },
        "industryDomain":   { "type": "keyword" },
        "location":         { "type": "keyword", "fields": { "text": { "type": "text", "analyzer": "korean_analyzer" }}},
        "skills":           { "type": "keyword" },
        "expYears":         { "type": "integer" },
        "salaryMin":        { "type": "integer" },
        "salaryMax":        { "type": "integer" },
        "sourceType":       { "type": "keyword" },
        "status":           { "type": "keyword" },
        "originUrl":        { "type": "keyword" },
        "deadlineAt":       { "type": "date" },
        "viewCount":        { "type": "integer" },
        "applicationCount": { "type": "integer" },
        "createdAt":        { "type": "date" },
        "updatedAt":        { "type": "date" }
      }
    }
  }
}
```

### 6.4 Redis 캐시 설계

| Key Pattern | Value Type | TTL | 용도 |
|-------------|-----------|-----|------|
| `search:{queryHash}` | JSON (검색 결과) | 5분 | 검색 결과 캐시 |
| `recommend:{userId}` | JSON (추천 목록) | 1시간 | 추천 결과 캐시 |
| `stats:overview` | JSON (전체 통계) | 5분 | 통계 대시보드 |
| `stats:company:{companyId}` | JSON (기업 통계) | 1시간 | 기업별 통계 |
| `stats:skill:ranking` | JSON (스킬 순위) | 30분 | 스킬 수요 순위 |
| `stats:industry:{name}` | JSON (산업 통계) | 1시간 | 산업별 통계 |
| `email:verify:{email}` | String (인증코드) | 10분 | 이메일 인증 |
| `email:attempt:{email}` | Integer (시도 횟수) | 10분 | 인증 시도 횟수 |
| `rate:{userId}:{endpoint}` | Integer (요청 횟수) | 1분 | Rate Limiting |
| `crawl:lock:{site}` | String (executionId) | 2시간 | 크롤러 분산 락 |
| `notification:unread:{userId}` | Integer (안읽은 수) | - | 알림 배지 |
| `es:sync:pending` | List (JobPostingChangedEvent JSON) | 24시간 | ES 장애 시 동기화 이벤트 큐 |

---

## 7. API Specification

### 7.1 신규 API 엔드포인트

#### 7.1.1 채용공고 검색 및 목록 (FR-5)

| Method | Endpoint | 설명 | 인증 | 우선순위 |
|--------|----------|------|------|---------|
| GET | `/api/v1/job-postings` | 채용공고 목록 조회 (커서 기반 페이징) | Optional | P0 |
| GET | `/api/v1/job-postings/search` | 전문 검색 + 필터링 | Optional | P0 |
| GET | `/api/v1/job-postings/search/suggest` | 검색어 자동완성 | Optional | P0 |

**GET /api/v1/job-postings 상세:**
```
Query Parameters:
  - cursor: String (다음 페이지 커서)
  - size: Integer (default 20, max 50)
  - sort: String (LATEST, POPULAR, DEADLINE, RELEVANCE)
  - skills: List<String> (스킬 필터)
  - expYears: Integer (경력 필터)
  - location: String (지역 필터)
  - industryDomain: String (산업 필터)
  - companyId: Long (기업 필터)
  - sourceType: String (DIRECT, CRAWLED)
  - status: String (OPEN, CLOSED)
  - salaryMin: Integer (최소 연봉 필터)
  - salaryMax: Integer (최대 연봉 필터)
  - fromDate: LocalDate (등록일 시작)
  - toDate: LocalDate (등록일 종료)

Response:
{
  "data": {
    "items": [JobPostingListResponse],
    "nextCursor": "string",
    "hasNext": boolean,
    "totalCount": long
  }
}
```

**GET /api/v1/job-postings/search 상세:**
```
Query Parameters:
  - q: String (검색어, 필수)
  - (위 필터 파라미터 동일)
  - fuzzy: Boolean (오타 보정 활성화, default true)

Response:
{
  "data": {
    "items": [JobPostingSearchResponse],
    "nextCursor": "string",
    "hasNext": boolean,
    "totalCount": long,
    "facets": {
      "skills": [{"name": "Java", "count": 150}],
      "locations": [{"name": "서울", "count": 300}],
      "industries": [{"name": "IT", "count": 200}]
    },
    "suggestions": ["대안 검색어"]
  }
}
```

#### 7.1.2 크롤링 관리 API (FR-1)

| Method | Endpoint | 설명 | 인증 | 우선순위 |
|--------|----------|------|------|---------|
| GET | `/api/v1/admin/crawling/status` | 크롤러 상태 조회 | Admin | P0 |
| GET | `/api/v1/admin/crawling/executions` | 크롤링 실행 이력 | Admin | P1 |
| POST | `/api/v1/admin/crawling/trigger` | 수동 크롤링 트리거 | Admin | P1 |
| PUT | `/api/v1/admin/crawling/{site}/config` | 크롤러 설정 변경 | Admin | P2 |

#### 7.1.3 트렌드 분석 API (FR-2)

| Method | Endpoint | 설명 | 인증 | 우선순위 |
|--------|----------|------|------|---------|
| GET | `/api/v1/trends/skills` | 스킬 수요 트렌드 | Optional | P1 |
| GET | `/api/v1/trends/skills/{skillName}/history` | 특정 스킬 히스토리 | Optional | P1 |
| GET | `/api/v1/trends/skills/cooccurrence` | 스킬 동시 출현 | Optional | P1 |
| GET | `/api/v1/trends/companies` | 기업별 채용 동향 | Optional | P1 |
| GET | `/api/v1/trends/companies/{companyId}/history` | 특정 기업 히스토리 | Optional | P1 |
| GET | `/api/v1/trends/industries` | 산업별 채용 추이 | Optional | P1 |
| GET | `/api/v1/trends/salary` | 급여 트렌드 | Optional | P2 |

#### 7.1.4 통계 API (FR-3)

| Method | Endpoint | 설명 | 인증 | 우선순위 |
|--------|----------|------|------|---------|
| GET | `/api/v1/statistics/overview` | 전체 채용 현황 | Optional | P1 |
| GET | `/api/v1/statistics/companies/{companyId}` | 기업별 통계 | Optional | P1 |
| GET | `/api/v1/statistics/industries` | 산업별 통계 | Optional | P1 |
| GET | `/api/v1/statistics/locations` | 지역별 통계 | Optional | P1 |
| GET | `/api/v1/statistics/skills/ranking` | 스킬 수요 순위 | Optional | P1 |

#### 7.1.5 추천 API (FR-4)

| Method | Endpoint | 설명 | 인증 | 우선순위 |
|--------|----------|------|------|---------|
| GET | `/api/v1/recommendations` | 개인 맞춤 추천 | Required | P1 |
| POST | `/api/v1/recommendations/feedback` | 추천 피드백 | Required | P2 |
| GET | `/api/v1/recommendations/popular` | 인기 공고 (비로그인) | Optional | P1 |
| POST | `/api/v1/onboarding` | 온보딩 질문 제출 | Required | P1 |
| GET | `/api/v1/onboarding` | 온보딩 상태 조회 | Required | P1 |

#### 7.1.6 기업측 지원자 관리 API (FR-6)

| Method | Endpoint | 설명 | 인증 | 우선순위 |
|--------|----------|------|------|---------|
| GET | `/api/v1/company/applications` | 전체 지원자 목록 | CompanyAdmin | P1 |
| GET | `/api/v1/company/job-postings/{id}/applications` | 공고별 지원자 | CompanyAdmin | P1 |
| GET | `/api/v1/company/applications/{id}/profile` | 지원자 프로필 상세 | CompanyAdmin | P1 |
| PATCH | `/api/v1/company/applications/{id}/status` | 지원 상태 변경 | CompanyAdmin | P1 |
| PATCH | `/api/v1/company/applications/bulk-status` | 벌크 상태 변경 | CompanyAdmin | P1 |

#### 7.1.7 알림 API (FR-7)

| Method | Endpoint | 설명 | 인증 | 우선순위 |
|--------|----------|------|------|---------|
| GET | `/api/v1/notifications` | 알림 목록 | Required | P2 |
| GET | `/api/v1/notifications/unread-count` | 안읽은 알림 수 | Required | P2 |
| PATCH | `/api/v1/notifications/{id}/read` | 알림 읽음 처리 | Required | P2 |
| PATCH | `/api/v1/notifications/read-all` | 전체 읽음 처리 | Required | P2 |
| GET | `/api/v1/notifications/settings` | 알림 설정 조회 | Required | P2 |
| PUT | `/api/v1/notifications/settings` | 알림 설정 변경 | Required | P2 |

#### 7.1.8 이메일 인증 API (FR-8)

| Method | Endpoint | 설명 | 인증 | 우선순위 |
|--------|----------|------|------|---------|
| POST | `/api/v1/auth/email/send-code` | 인증 코드 발송 | Optional | P2 |
| POST | `/api/v1/auth/email/verify` | 인증 코드 확인 | Optional | P2 |

### 7.2 API 총 규모

| 카테고리 | 신규 엔드포인트 수 | 우선순위 |
|----------|-------------------|---------|
| 검색/목록 | 3 | P0 |
| 크롤링 관리 | 4 | P0-P2 |
| 트렌드 분석 | 7 | P1-P2 |
| 통계 | 5 | P1 |
| 추천 | 5 | P1-P2 |
| 기업 지원자 관리 | 5 | P1 |
| 알림 | 6 | P2 |
| 이메일 인증 | 2 | P2 |
| **합계** | **37** | - |

---

## 8. Technology Stack Additions

### 8.1 신규 도입 기술

| 기술 | 버전 | 용도 | 도입 단계 | 라이선스 |
|------|------|------|----------|---------|
| **Elasticsearch** | 8.x | 전문 검색, 인덱싱 | Phase 1 | Elastic License 2.0 / SSPL |
| **Spring Data Elasticsearch** | 5.x | ES 클라이언트 | Phase 1 | Apache 2.0 |
| **Nori (Korean Analyzer)** | ES 내장 | 한국어 형태소 분석 | Phase 1 | Elastic License 2.0 |
| **Spring Batch** | 5.x | 크롤링/분석 배치 | Phase 1 | Apache 2.0 |
| **Jsoup** | 1.17.x | HTML 파싱 (잡코리아 폴백 전용) | Phase 1 | MIT |
| **Spring WebFlux (WebClient)** | 3.x | 비동기 REST API 호출 (사람인/원티드/잡코리아 API) | Phase 1 | Apache 2.0 |
| **Spring Mail** | 3.x | 이메일 발송 | Phase 3 | Apache 2.0 |
| **Resilience4j** | 2.x | Circuit Breaker, Rate Limiter | Phase 1 | Apache 2.0 |
| **Smile Core** | 3.1.1 | TF-IDF, 코사인 유사도, 희소 행렬 연산 | Phase 2 | Apache 2.0 |
| **Smile NLP** | 3.1.1 | 텍스트 토큰화, NLP 유틸리티 | Phase 2 | Apache 2.0 |

> **참고:** 한국어 토큰화는 Elasticsearch nori 분석기(이미 도입 예정)로 수행하고, 토큰화된 결과를 Smile TF-IDF 입력으로 사용한다. Selenium은 현 단계에서 도입하지 않으며, 잡코리아 API 평가 후 동적 페이지 크롤링이 반드시 필요한 경우에만 추후 검토한다.

### 8.2 인프라 변경

| 컴포넌트 | 현재 | 변경 후 | 비고 |
|----------|------|--------|------|
| Elasticsearch | 없음 | Docker 컨테이너 (3노드 클러스터) | 검색/인덱싱 |
| MySQL | 단일 인스턴스 | + Read Replica 1대 | 읽기 분산 |
| Redis | 캐시 용도 | + 분산 락, Rate Limiting, 이벤트 큐 | 역할 확장 |
| Spring Batch | 없음 | 모놀리스 내장 (api/crawling/ 패키지) | 별도 마이크로서비스가 아닌 모놀리스에 포함. Selenium 필요 시 Selenium Grid/Selenoid Docker로 원격 실행 |

### 8.3 build.gradle 추가 의존성

```groovy
// Elasticsearch
implementation 'org.springframework.boot:spring-boot-starter-data-elasticsearch'

// Spring Batch
implementation 'org.springframework.boot:spring-boot-starter-batch'

// WebClient (비동기 REST API 호출 - 사람인/원티드/잡코리아 API)
implementation 'org.springframework.boot:spring-boot-starter-webflux'

// Crawling (잡코리아 폴백 전용)
implementation 'org.jsoup:jsoup:1.17.2'
// NOTE: selenium-java는 현 단계에서 도입하지 않음. 잡코리아 API 평가 후 필요 시 추가

// Email
implementation 'org.springframework.boot:spring-boot-starter-mail'

// Resilience4j
implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.2.0'

// ML/NLP (TF-IDF, Cosine Similarity, Sparse Matrices)
implementation 'com.github.haifengl:smile-core:3.1.1'
implementation 'com.github.haifengl:smile-nlp:3.1.1'
// NOTE: Apache Commons Math 제거 - Smile이 TF-IDF, 코사인 유사도, 희소 행렬을 모두 지원

// Scheduler
implementation 'net.javacrumbs.shedlock:shedlock-spring:5.12.0'
implementation 'net.javacrumbs.shedlock:shedlock-provider-redis-spring:5.12.0'
```

---

## 9. Implementation Phases

### Phase 1: 기반 구축 및 검색 (4주)

**목표:** 사용자가 채용공고를 탐색할 수 있는 최소 기능 제공

| 주차 | 작업 | 산출물 | 관련 FR |
|------|------|--------|--------|
| 1주 | Elasticsearch 인프라 구축, Docker 설정 | ES 클러스터 + nori 분석기 | FR-5.1 |
| 1주 | JobPosting 테이블 확장 (salary, location, deadline 등) | DB 마이그레이션 | FR-5.2 |
| 2주 | 검색 API 구현 (전문 검색 + 필터링 + 페이징) | 3개 API 엔드포인트 | FR-5.1, FR-5.2, FR-5.3 |
| 2주 | MySQL -> ES 동기화 파이프라인 | 실시간 인덱싱 | FR-5.1 |
| 3주 | 데이터 수집 엔진 기반 (Spring Batch + WebClient + Jsoup) | 수집 프레임워크 (api/crawling/) | FR-1.1 |
| 3주 | 사람인 API 어댑터 구현 (SaraminApiAdapter) | 1개 사이트 API 수집 | FR-1.1 |
| 4주 | 원티드 API 어댑터 (WantedApiAdapter) + 잡코리아 하이브리드 어댑터 (JobKoreaHybridAdapter) | 3개 사이트 수집 | FR-1.1 |
| 4주 | 중복 제거 + 정규화 파이프라인 | 수집 데이터 -> JobPosting | FR-1.3 |

**Phase 1 완료 조건:**
- 채용공고 검색이 200ms 이내 동작
- 3개 사이트에서 API/크롤링 기반 데이터 수집 성공
- 수집된 공고가 MySQL -> ES 동기화를 거쳐 검색 결과에 포함
- MySQL-ES 동기화 지연 30초 이내

### Phase 2: 분석 및 추천 (8주)

**목표:** 데이터 기반 인사이트 및 개인화 추천 제공

| 주차 | 작업 | 산출물 | 관련 FR |
|------|------|--------|--------|
| 5주 | 수집 스케줄링 + 모니터링 | Hot/Warm/Cold 스케줄러 | FR-1.2, FR-1.4 |
| 5주 | 법적 준수 모듈 (robots.txt, rate limiting, ToS 검증) | 법적 안전장치 | FR-1.5 |
| 6주 | 시계열 데이터 수집 파이프라인 | daily_statistics 배치 | FR-2.1 |
| 6주 | 통계 API 구현 (전체/기업/산업/지역/스킬) | 5개 API 엔드포인트 | FR-3.1~FR-3.4 |
| 7주 | 스킬 수요 트렌드 분석 | 스킬 트렌드 API | FR-2.2 |
| 7주 | 기업/산업별 채용 동향 분석 | 기업/산업 트렌드 API | FR-2.3 |
| 8주 | 콘텐츠 기반 추천 엔진 (Smile TF-IDF + 코사인 유사도) | 추천 API v1 | FR-4.1 |
| 9주 | 온보딩 + Cold Start 해결 | 온보딩 API | FR-4.4 |
| 9주 | 추천 모델 오프라인 평가 (히스토리컬 데이터 기반) | 정확도 리포트 | FR-4.1 |
| 10주 | 협업 필터링 구현 | 추천 모델 v2 | FR-4.2 |
| 10주 | 하이브리드 추천 + A/B 테스트 프레임워크 | 추천 API v2 | FR-4.3 |
| 11주 | 추천 피드백 + 학습 루프 | 피드백 API | FR-4.5 |
| 12주 | 급여 트렌드 분석 | 급여 트렌드 API | FR-2.4 |

**Phase 2 완료 조건:**
- 추천 결과 500ms 이내 반환
- 통계/트렌드 API 전체 동작
- 수집 스케줄러 자동 실행
- 추천 모델 오프라인 평가 완료 (precision@10 >= 0.15)

### Phase 3: 운영 기능 및 안정화 (4주)

**목표:** 기업측 기능, 알림, 이메일 인증 등 운영 완성도 향상

| 주차 | 작업 | 산출물 | 관련 FR |
|------|------|--------|--------|
| 13주 | 기업측 지원자 관리 API | 5개 API 엔드포인트 | FR-6.1~FR-6.3 |
| 13주 | 지원 상태 관리 + 이력 추적 | 상태 변경 이력 | FR-6.2 |
| 14주 | 알림 인프라 (Spring Event + 비동기) | 알림 시스템 기반 | FR-7.1 |
| 14주 | 알림 이벤트 5종 구현 | 6개 API 엔드포인트 | FR-7.2 |
| 15주 | 이메일 인증 (가입/변경) | 2개 API 엔드포인트 | FR-8.1, FR-8.2 |
| 15주 | PIPA 준수 (동의 관리, 데이터 보존/익명화) | 컴플라이언스 모듈 | NFR-5 |
| 16주 | 부하 테스트 + 성능 최적화 | 10,000 동시 사용자 검증 | NFR-1, NFR-2 |
| 16주 | 보안 점검 + Rate Limiting 최종 조정 | 보안 강화 | NFR-3 |

**Phase 3 완료 조건:**
- 기업 관리자가 지원자를 관리할 수 있음
- 알림이 실시간으로 발송됨
- 10,000 동시 사용자 부하 테스트 통과

### Phase 요약

```
Phase 1 (4주):  검색 + 데이터 수집 기반      --> 사용자가 공고를 찾을 수 있다
Phase 2 (8주):  분석 + 추천                 --> 데이터 기반 인사이트와 개인화
Phase 3 (4주):  운영 기능 + 안정화            --> 프로덕션 수준의 완성도
총 16주 (약 4개월)
```

### 9.5 팀 구성 및 용량 계획

#### 권장 팀 구성 (16주 타임라인 기준)

| 역할 | 인원 | 담당 영역 |
|------|------|----------|
| **백엔드 시니어 개발자** | 1명 | 추천 엔진, Elasticsearch 통합, 아키텍처 리드 |
| **백엔드 미드레벨 개발자** | 2명 | 데이터 수집 어댑터, 트렌드/통계 API, 알림 시스템 |
| **DevOps/인프라 엔지니어** | 1명 | ES 클러스터, Docker, CI/CD, 모니터링 |

**최소 구성:** 3명 백엔드 개발자 + 1명 DevOps = 4명

#### 팀 규모별 타임라인 조정

| 팀 규모 | 예상 기간 | 비고 |
|---------|----------|------|
| 4명 (3 백엔드 + 1 DevOps) | 16주 | 기본 계획 |
| 3명 (2 백엔드 + 1 DevOps) | 22주 | Phase 2의 병렬 작업 불가, 순차 진행 |
| 2명 (1 백엔드 + 1 DevOps) | 30주+ | 추천 시스템 단순화 필요, P2 기능 제외 권장 |
| 1명 (풀스택) | 40주+ | 비현실적, P0만 구현하고 P1 이상 제외 권장 |

#### 페이즈별 역할 배정

| Phase | 시니어 | 미드레벨 A | 미드레벨 B | DevOps |
|-------|--------|-----------|-----------|--------|
| Phase 1 (1-4주) | ES 통합 + 동기화 | 사람인 API 어댑터 | 원티드/잡코리아 어댑터 | ES 클러스터 + Docker |
| Phase 2 (5-12주) | 추천 엔진 (TF-IDF, 협업 필터링) | 트렌드/통계 API | 스케줄링 + 법적 준수 | 모니터링 + 배치 인프라 |
| Phase 3 (13-16주) | 코드 리뷰 + 성능 최적화 | 지원자 관리 API | 알림 + 이메일 인증 | 부하 테스트 + 보안 |

---

## 10. Risk Analysis

### 10.1 기술적 리스크

| ID | 리스크 | 발생 확률 | 영향도 | 완화 전략 |
|----|--------|----------|--------|----------|
| TR-1 | 크롤링 대상 사이트 구조 변경으로 크롤러 파손 | 높음 | 높음 | 사이트별 독립 어댑터 + 구조 변경 감지 알림 + 2주 단위 크롤러 점검 |
| TR-2 | Elasticsearch 클러스터 장애 | 중간 | 높음 | MySQL 대체 쿼리 모드 + ES 모니터링 알림 + 정기 스냅샷 |
| TR-3 | 크롤링 차단 (IP 블랙리스트) | 중간 | 중간 | IP 로테이션 + 요청 간격 조절 + 프록시 풀 |
| TR-4 | 추천 모델 정확도 부족 (Cold Start 기간) | 높음 | 중간 | 인기도 기반 fallback + 온보딩으로 초기 데이터 수집 |
| TR-5 | 대용량 데이터 처리 시 배치 지연 | 중간 | 중간 | 파티셔닝 + 병렬 처리 + 타임아웃 설정 |
| TR-6 | MySQL-ES 데이터 동기화 불일치 | 중간 | 높음 | Spring ApplicationEvent 기반 동기화 (Section 6.2) + 주간 정합성 체크 배치 + ES 장애 시 Redis 큐 폴백 |

### 10.2 비즈니스 리스크

| ID | 리스크 | 발생 확률 | 영향도 | 완화 전략 |
|----|--------|----------|--------|----------|
| BR-1 | 크롤링 법적 이슈 (저작권 침해 소송) | 낮음 | 매우 높음 | robots.txt 준수 + 단일 소스 30% 미만 + 법률 자문 |
| BR-2 | 개인정보보호법 위반 | 낮음 | 매우 높음 | PIPA 체크리스트 + 정기 감사 + DPO 지정 |
| BR-3 | 크롤링 대상 사이트의 ToS 위반 통보 | 중간 | 높음 | 사전 법률 검토 + 즉시 대응 절차 + 대체 소스 확보 |
| BR-4 | 추천 품질 저하로 사용자 이탈 | 중간 | 높음 | A/B 테스트 + 피드백 루프 + 품질 모니터링 KPI |
| BR-5 | 인프라 비용 증가 (ES, 크롤링 서버) | 중간 | 중간 | 리소스 모니터링 + 비용 알림 + 오토스케일링 |

### 10.3 리스크 대응 매트릭스

```
                높음 │  TR-1       BR-1, BR-2
        영향도       │  TR-6       BR-3
                     │  TR-4,TR-5  BR-4, BR-5
                낮음 │
                     └──────────────────────
                      낮음    중간    높음
                           발생 확률
```

**즉시 대응 (높은 영향도 + 중간 이상 발생 확률):** TR-1, TR-6, BR-3
**예방적 관리 (높은 영향도 + 낮은 발생 확률):** BR-1, BR-2
**정기 모니터링 (중간 영향도):** TR-4, TR-5, BR-4, BR-5

### 10.4 롤백 계획 (Rollback Plan)

각 신규 기능은 Spring Properties 기반 Feature Flag로 제어하여 개별적으로 비활성화할 수 있다.

```yaml
# application.yml
feature:
  crawling:
    enabled: true           # 데이터 수집 시스템 전체 on/off
    saramin-enabled: true   # 사람인 어댑터
    wanted-enabled: true    # 원티드 어댑터
    jobkorea-enabled: true  # 잡코리아 어댑터
  search:
    elasticsearch-enabled: true  # false 시 MySQL LIKE 쿼리 폴백
  recommendation:
    enabled: true           # 추천 시스템 전체 on/off
    collaborative-enabled: true  # 협업 필터링 (콘텐츠 기반만 사용 가능)
  trend:
    enabled: true           # 트렌드 분석
  notification:
    enabled: true           # 알림 시스템
    email-enabled: true     # 이메일 알림만 별도 제어
```

**롤백 절차:**
1. 장애 감지 시 해당 Feature Flag를 `false`로 변경
2. Spring Actuator `/actuator/refresh` 호출 또는 재배포
3. 해당 기능의 API는 `503 Service Unavailable` 반환
4. 근본 원인 수정 후 Feature Flag 재활성화

---

## 10.5 에러 응답 규격

기존 `ApiResponse` 패턴을 따르며, 신규 기능에 대한 에러 코드를 추가한다.

```json
{
  "success": false,
  "message": "에러 메시지",
  "data": null,
  "errorCode": "CRAWLING_001"
}
```

| 도메인 | 에러 코드 | HTTP 상태 | 설명 |
|--------|----------|-----------|------|
| 수집 | CRAWLING_001 | 500 | 크롤러 실행 실패 |
| 수집 | CRAWLING_002 | 429 | 크롤링 Rate Limit 초과 |
| 수집 | CRAWLING_003 | 403 | robots.txt 접근 거부 |
| 검색 | SEARCH_001 | 503 | Elasticsearch 연결 실패 |
| 검색 | SEARCH_002 | 400 | 잘못된 검색 쿼리 |
| 추천 | RECOMMEND_001 | 404 | 추천 결과 없음 (프로필 미완성) |
| 추천 | RECOMMEND_002 | 503 | 추천 엔진 일시 장애 |
| 지원자 관리 | COMPANY_APP_001 | 403 | CompanyMember 권한 부족 |
| 지원자 관리 | COMPANY_APP_002 | 400 | 잘못된 상태 전이 |
| 알림 | NOTIFICATION_001 | 500 | 알림 발송 실패 |
| 이메일 인증 | EMAIL_001 | 429 | 인증 시도 횟수 초과 |
| 이메일 인증 | EMAIL_002 | 400 | 만료된 인증 코드 |

---

## 10.6 인가 메커니즘

기업측 API의 권한 검증은 `@PreAuthorize` 어노테이션과 커스텀 `CompanyAdminChecker` 서비스를 통해 구현한다.

```java
// 사용 예시
@PreAuthorize("@companyAdminChecker.isAdmin(authentication, #companyId)")
@GetMapping("/api/v1/company/applications")
public ApiResponse<Page<ApplicationResponse>> getApplications(...) { ... }
```

| 역할 | 접근 가능 API | 검증 로직 |
|------|-------------|----------|
| CompanyMember.role = ADMIN | 지원자 목록, 상태 변경, 프로필 열람, 벌크 처리 | `CompanyAdminChecker.isAdmin()` |
| CompanyMember.role = MEMBER | 지원자 목록 (읽기 전용) | `CompanyAdminChecker.isMember()` |
| CompanyMember.role = VIEWER | 지원자 목록 (읽기 전용, 개인정보 마스킹) | `CompanyAdminChecker.isViewer()` |
| User.role = ADMIN | 크롤링 관리 API 전체 | `@PreAuthorize("hasRole('ADMIN')")` |

---

## 10.7 테스트 전략

### 10.7.1 단위 테스트 (Unit Tests)

| 항목 | 도구 | 커버리지 목표 | 대상 |
|------|------|-------------|------|
| 서비스 레이어 | JUnit 5 + Mockito | 80% 이상 (line coverage) | 모든 신규 UseCase/Service 클래스 |
| 도메인 로직 | JUnit 5 | 90% 이상 | 추천 점수 계산, 중복 감지, 정규화 로직 |
| 어댑터 | JUnit 5 + Mockito | 70% 이상 | API 어댑터, ES 리포지토리 |

### 10.7.2 통합 테스트 (Integration Tests)

| 항목 | 도구 | 대상 |
|------|------|------|
| MySQL 통합 | Testcontainers (MySQL) | JPA 리포지토리, 배치 Job |
| Redis 통합 | Testcontainers (Redis) | 캐시, 분산 락, Rate Limiting |
| Elasticsearch 통합 | Testcontainers (Elasticsearch) | 인덱싱, 검색, 동기화 |
| 전체 슬라이스 | @WebMvcTest + @DataJpaTest | 컨트롤러 -> 서비스 -> 리포지토리 |

### 10.7.3 데이터 수집 테스트

| 항목 | 도구 | 대상 |
|------|------|------|
| HTTP 응답 모킹 | WireMock | API 어댑터 (사람인, 원티드, 잡코리아 API 응답 시뮬레이션) |
| HTML 파싱 | 정적 HTML fixtures | Jsoup 파싱 로직 (잡코리아 폴백) |
| 정규화 | JUnit 5 | 다양한 포맷의 원본 데이터 -> JobPosting 변환 검증 |

### 10.7.4 추천 모델 검증

| 항목 | 방법 | 기준 |
|------|------|------|
| 오프라인 평가 | 히스토리컬 Application 데이터 기반 train/test split (80/20) | Precision@10 >= 0.15, Recall@20 >= 0.10 |
| A/B 테스트 | 추천 vs 인기도 기반 비교 | 추천 그룹 CTR이 인기도 그룹 대비 20% 이상 |
| 다양성 측정 | ILS (Intra-List Similarity) | 협업 필터링 추가 시 ILS 20% 감소 |

### 10.7.5 부하 테스트

| 항목 | 도구 | 시나리오 | 통과 기준 |
|------|------|---------|----------|
| 검색 API | k6 | 1,000 req/s 지속 5분 | p95 < 200ms, 에러율 < 0.1% |
| 추천 API | k6 | 500 req/s 지속 5분 | p95 < 500ms, 에러율 < 0.1% |
| 동시 접속 | Gatling | 10,000 동시 사용자 시뮬레이션 | 에러율 < 1%, 평균 응답 < 500ms |
| 배치 처리 | 직접 측정 | 10만 건 데이터 수집/인덱싱 | 30분 이내 완료 |

### 10.7.6 E2E API 테스트

모든 37개 신규 엔드포인트에 대한 통합 API 테스트:
- Spring Boot Test (`@SpringBootTest` + `TestRestTemplate`)
- Testcontainers로 전체 인프라 (MySQL + Redis + ES) 구동
- 시나리오 기반: 가입 -> 온보딩 -> 검색 -> 추천 -> 지원 -> 상태변경 -> 알림 확인

---

## 10.8 신규 모듈 구조 (헥사고날 아키텍처)

모든 신규 모듈은 기존 프로젝트의 헥사고날 아키텍처 패턴을 따른다.

```
api/
  crawling/                    # 데이터 수집 모듈
    adapter/
      in/web/CrawlingAdminController.java
      out/saramin/SaraminApiAdapter.java
      out/wanted/WantedApiAdapter.java
      out/jobkorea/JobKoreaHybridAdapter.java
    application/
      port/in/TriggerCrawlingUseCase.java
      port/out/SaveRawDataPort.java
      service/CrawlingService.java
    domain/RawCrawledData.java

  trend/                       # 트렌드 분석 모듈
    adapter/
      in/web/TrendController.java
      out/persistence/DailyStatisticsRepository.java
    application/
      port/in/GetSkillTrendUseCase.java
      service/TrendAnalysisService.java
    domain/DailyStatistic.java

  statistics/                  # 통계 모듈
    adapter/
      in/web/StatisticsController.java
    application/
      service/StatisticsService.java

  recommendation/              # 추천 모듈
    adapter/
      in/web/RecommendationController.java
      out/persistence/RecommendationCacheRepository.java
    application/
      port/in/GetRecommendationsUseCase.java
      service/ContentBasedRecommendationService.java
      service/CollaborativeFilteringService.java
      service/HybridRecommendationService.java
    domain/RecommendationCache.java

  notification/                # 알림 모듈
    adapter/
      in/web/NotificationController.java
      out/email/EmailSender.java
    application/
      port/in/GetNotificationsUseCase.java
      service/NotificationService.java
      listener/ApplicationStatusListener.java
    domain/Notification.java
```

---

## 11. Success Metrics (KPIs)

### 11.1 플랫폼 핵심 지표

| KPI | 정의 | 목표값 | 측정 주기 | 측정 방법 |
|-----|------|--------|----------|----------|
| DAU (Daily Active Users) | 일일 순 방문자 수 | 런칭 3개월 후 1,000명 | 일별 | Google Analytics / 자체 로그 |
| 검색 전환율 | 검색 -> 공고 클릭 비율 | 30% 이상 | 주별 | user_events 분석 |
| 지원 전환율 | 공고 조회 -> 지원 비율 | 5% 이상 | 주별 | Application / ViewEvent 비율 |
| 추천 클릭률 (CTR) | 추천 공고 클릭 비율 | 15% 이상 | 주별 | user_events(CLICK) / 추천 노출 |
| 추천 지원률 | 추천 공고 지원 비율 | 3% 이상 | 주별 | user_events(APPLY) / 추천 노출 |
| 크롤링 수집량 | 일일 신규 수집 공고 수 | 500건 이상 | 일별 | crawl_executions 집계 |

### 11.2 기술 성능 지표

| KPI | 정의 | 목표값 | 측정 주기 | 측정 방법 |
|-----|------|--------|----------|----------|
| 검색 응답 시간 (p95) | 검색 API 95번째 백분위 응답 | < 200ms | 실시간 | Micrometer Timer |
| 추천 응답 시간 (p95) | 추천 API 95번째 백분위 응답 | < 500ms | 실시간 | Micrometer Timer |
| 서비스 가용성 | 월간 가용률 | 99.5% | 월별 | Uptime 모니터링 |
| 크롤링 성공률 | 크롤링 시도 대비 성공 비율 | 95% 이상 | 일별 | crawl_executions |
| 크롤링 정규화 성공률 | 원본 대비 정규화 성공 비율 | 95% 이상 | 일별 | raw_crawled_data.processed |
| ES 인덱싱 지연 | MySQL 변경 -> ES 반영 시간 | < 30초 | 실시간 | Custom Metric |

### 11.3 데이터 품질 지표

| KPI | 정의 | 목표값 | 측정 주기 | 측정 방법 |
|-----|------|--------|----------|----------|
| 크롤링 중복률 | 전체 크롤링 대비 중복 비율 | < 20% | 주별 | crawl_executions.total_duplicate |
| 스킬 추출 정확도 | 공고에서 올바른 스킬 추출 비율 | 90% 이상 | 월별 | 수동 샘플링 검증 (100건) |
| 급여 추출 정확도 | 급여 정보 올바른 파싱 비율 | 80% 이상 | 월별 | 수동 샘플링 검증 (100건) |
| 검색 관련도 | 사용자 검색 만족도 (설문) | 4.0/5.0 이상 | 분기별 | 사용자 설문 |

---

## 12. Acceptance Criteria

### 12.1 FR별 인수 테스트 시나리오

#### AC-FR-1: 채용공고 크롤링 시스템

```
GIVEN 크롤링 시스템이 구동 중일 때
WHEN  사람인에서 "Java 백엔드 개발자" 공고 100건이 존재하면
THEN  크롤러가 100건을 수집하고
  AND raw_crawled_data에 원본이 저장되고
  AND job_postings에 sourceType=CRAWLED로 정규화 저장되고
  AND 중복 URL은 건너뛰고
  AND 스킬(Java, Spring 등)이 자동 추출되어 job_skills에 연결되고
  AND 크롤링 실행 이력에 결과가 기록된다

GIVEN 크롤링 스케줄러가 설정되어 있을 때
WHEN  Hot 스케줄 시간(6시간)이 경과하면
THEN  자동으로 크롤링이 실행되고
  AND 이전 크롤링 이후 신규/변경된 공고만 처리되고
  AND 크롤링 소요 시간이 30분을 초과하지 않는다

GIVEN 크롤링 대상 사이트에 robots.txt가 있을 때
WHEN  크롤링을 시작하면
THEN  robots.txt를 파싱하여 허용된 경로만 접근하고
  AND 요청 간격이 최소 2초 이상이고
  AND User-Agent에 봇 식별 정보가 포함된다
```

#### AC-FR-2: 채용 시장 트렌드 분석

```
GIVEN 30일 이상의 크롤링 데이터가 축적되었을 때
WHEN  GET /api/v1/trends/skills를 호출하면
THEN  스킬별 주간 수요 변화율이 반환되고
  AND 3개월 이동평균 대비 20% 이상 증가한 스킬이 "emerging"으로 표시되고
  AND 응답 시간이 500ms 이내이다

GIVEN 기업 A의 채용공고가 전월 대비 50% 증가했을 때
WHEN  GET /api/v1/trends/companies/{companyId}/history를 호출하면
THEN  월별 공고 수 추이가 반환되고
  AND "significant_change" 플래그가 true이고
  AND 변화율이 정확하게 계산된다
```

#### AC-FR-3: 채용공고 통계

```
GIVEN 활성 채용공고 1,000건이 존재할 때
WHEN  GET /api/v1/statistics/overview를 호출하면
THEN  전체 활성 공고 수, 오늘 신규, 소스별 비율이 반환되고
  AND 응답 시간이 200ms 이내이고
  AND 캐시 TTL 5분 동안 동일 결과를 반환한다
```

#### AC-FR-4: 맞춤형 추천

```
GIVEN 사용자가 Java, Spring, AWS 스킬을 보유하고 3년 경력일 때
WHEN  GET /api/v1/recommendations를 호출하면
THEN  Java/Spring/AWS 관련 공고가 상위에 랭크되고
  AND 각 항목에 추천 이유(reason)가 포함되고
  AND 추천 결과 20건이 500ms 이내 반환되고
  AND 경력 2~5년 범위 공고가 우선 매칭된다

GIVEN 신규 가입 사용자 (프로필 없음)일 때
WHEN  GET /api/v1/recommendations를 호출하면
THEN  인기도 기반 추천이 반환되고 (인기 공고)
  AND 온보딩 질문 완료 안내가 포함된다

GIVEN 사용자가 온보딩 질문을 완료했을 때
WHEN  POST /api/v1/onboarding에 희망 직군, 스킬 3개, 경력 수준, 지역을 제출하면
THEN  즉시 콘텐츠 기반 추천이 생성되고
  AND GET /api/v1/recommendations에서 개인화된 결과를 반환한다
```

#### AC-FR-5: 검색 및 필터링

```
GIVEN 10만 건의 채용공고가 인덱싱되어 있을 때
WHEN  GET /api/v1/job-postings/search?q=자바 백엔드를 호출하면
THEN  "자바", "Java", "백엔드", "Backend" 관련 공고가 반환되고 (한국어 형태소 분석)
  AND facet 정보(스킬별/지역별/산업별 건수)가 포함되고
  AND 응답 시간이 200ms 이내이다

GIVEN 검색어 "스프링부트"를 입력할 때 (오타)
WHEN  검색을 실행하면
THEN  "스프링부트" 및 "스프링 부트" 결과를 모두 반환한다

GIVEN 필터 조건 skills=Java,Spring AND location=서울 AND expYears=3일 때
WHEN  목록 조회를 실행하면
THEN  3가지 조건을 모두 만족하는 공고만 반환되고
  AND 커서 기반 페이징이 정상 동작하고
  AND 10만 건에서 어떤 페이지든 200ms 이내이다
```

#### AC-FR-6: 기업측 지원자 관리

```
GIVEN 기업 관리자(CompanyMember.role=ADMIN)가 로그인했을 때
WHEN  GET /api/v1/company/applications를 호출하면
THEN  해당 기업의 공고에 지원한 지원자 목록이 반환되고
  AND 상태별(APPLIED/VIEWED/PASSED/REJECTED) 필터링이 가능하다

GIVEN 기업 관리자가 지원자 프로필을 처음 열람할 때
WHEN  GET /api/v1/company/applications/{id}/profile을 호출하면
THEN  지원자의 이력서, 포트폴리오, 학력, 경력, 스킬이 통합 반환되고
  AND 지원 상태가 자동으로 VIEWED로 변경되고
  AND 열람 로그가 기록된다

GIVEN 기업 관리자가 다수 지원자를 합격 처리할 때
WHEN  PATCH /api/v1/company/applications/bulk-status에 10명의 ID와 PASSED를 제출하면
THEN  10명의 상태가 모두 PASSED로 변경되고
  AND 각 지원자에게 상태 변경 알림이 발송된다
```

#### AC-FR-7: 알림 시스템

```
GIVEN 기업 관리자가 지원자 상태를 PASSED로 변경했을 때
THEN  해당 지원자에게 인앱 알림이 생성되고
  AND 이메일 알림이 발송되고 (이메일 설정 활성 시)
  AND 안읽은 알림 건수가 1 증가한다

GIVEN 사용자가 알림 목록을 조회할 때
WHEN  GET /api/v1/notifications를 호출하면
THEN  최신순으로 알림 목록이 반환되고
  AND 읽음/안읽음 상태가 표시된다
```

#### AC-FR-8: 이메일 인증

```
GIVEN 일반 회원가입(non-OAuth2) 시
WHEN  POST /api/v1/auth/email/send-code에 이메일을 제출하면
THEN  6자리 인증 코드가 이메일로 발송되고
  AND Redis에 10분 TTL로 저장된다

GIVEN 인증 코드를 3회 연속 틀렸을 때
WHEN  4번째 인증을 시도하면
THEN  "인증 코드를 재발송해 주세요" 오류가 반환된다
```

### 12.2 NFR 인수 조건 요약

| NFR | 인수 테스트 | 통과 기준 |
|-----|-----------|----------|
| NFR-1.1 | k6 부하 테스트: 1000 req/s 검색 API | p95 < 200ms |
| NFR-1.2 | k6 부하 테스트: 500 req/s 추천 API | p95 < 500ms |
| NFR-2.1 | Gatling 동시 접속 테스트 | 10,000명 동시 접속 시 오류율 < 1% |
| NFR-2.2 | 100만 건 데이터 로드 테스트 | 모든 API 정상 동작 |
| NFR-3.3 | Rate Limiting 테스트 | 초과 요청 시 429 응답 |
| NFR-4.1 | Chaos Engineering (ES 노드 kill) | MySQL 대체 모드 자동 전환 |
| NFR-5.2 | PIPA 체크리스트 감사 | 전 항목 통과 |

---

## Appendix A: 용어 정의

| 용어 | 정의 |
|------|------|
| PIPA | 개인정보 보호법 (Personal Information Protection Act) |
| Cold Start | 신규 사용자/아이템에 대한 추천 데이터 부족 문제 |
| TF-IDF | Term Frequency - Inverse Document Frequency (텍스트 벡터화 기법) |
| CDC | Change Data Capture (데이터 변경 감지) |
| Facet | 검색 결과의 차원별 집계 (필터 옵션에 해당 건수 표시) |
| Nori | Elasticsearch 한국어 형태소 분석기 |
| HPA | Horizontal Pod Autoscaler (Kubernetes 수평 자동 확장) |
| DPO | Data Protection Officer (개인정보 보호 책임자) |

## Appendix B: 참고 법률

| 법률 | 관련 조항 | 적용 영역 |
|------|----------|----------|
| 개인정보 보호법 | 제15조 (수집/이용), 제21조 (파기) | 사용자 데이터, 추천 |
| 저작권법 | 제101조의3 (데이터베이스 보호) | 크롤링 |
| 정보통신망법 | 제44조의7 (정보의 수집 제한) | 크롤링 |
| 관련 판례 | 공개 데이터 수집 관련 판례 (데이터베이스 실질적 복제 금지 원칙) | 크롤링 법적 근거 |

---

**문서 끝**

> 이 문서는 Techeer Resume 플랫폼의 v2.0 요구사항을 정의하며, 구현 단계 진입 전 모든 이해관계자의 검토와 승인이 필요합니다.
