# DDD Context Map: Techeer Resume → Job Aggregator Platform

> **Status**: Draft v1
> **Author**: sanghun
> **Created**: 2026-04-01
> **Sprint**: 0b (BE-0.4)

---

## 1. Bounded Contexts

### 1.1 User BC (기존 — Hex Architecture 유지)

**역할**: 사용자 인증, 프로필 관리, OAuth2 소셜 로그인
**성숙도**: 기존 Hex Architecture (Port & Adapter)
**전략**: 수요 기반 강화 (Demand-Driven Enrichment) — 새 기능이 요구할 때만 확장

**핵심 도메인 객체**
- `User` (Entity): id, email, nickname, role, profileImageUrl
- `UserProfile` (확장 대상): desiredPosition, experienceLevel, skills, preferredLocations, preferredCompanySize, salaryExpectation, openToRemote, profileCompleteness
- Ports: `LoadUserPort`, `SaveUserPort`, `LoadUserProfilePort`, `SaveUserProfilePort`

---

### 1.2 JobSearch BC (신규 — Core Domain)

**역할**: 채용 공고 수집(크롤링/API), OpenSearch 인덱싱, 검색 쿼리 처리
**성숙도**: 신규 DDD Aggregate 설계
**전략**: Full DDD — Aggregate, VO, Domain Event, Repository Port

**핵심 도메인 객체**
- `JobPosting` (Aggregate Root): 채용 공고 전체 생명주기 관리
- Value Objects: `SalaryRange`, `SourceInfo`, `CompanyInfo`
- Domain Events: `JobPostingCreatedEvent`, `JobPostingUpdatedEvent`, `JobPostingExpiredEvent`
- Supporting: `JobCrawler` (Strategy Interface), `JobSearchService`, `JobIndexingService`

---

### 1.3 Recommendation BC (신규 — Core Domain)

**역할**: 규칙 기반 매칭 점수 산출, 개인화 추천 피드, Top-N 추천 저장
**성숙도**: 신규 DDD Aggregate 설계
**전략**: Full DDD — 순수 도메인 로직, 외부 의존성 격리

**핵심 도메인 객체**
- `Recommendation` (Aggregate Root): 사용자-공고 매칭 결과
- `UserEvent` (Standalone Entity — Event Sourcing Lite): 사용자 행동 이벤트 로그
- Value Objects: `MatchScore`
- Supporting: `RecommendationEngine`, `MatchScoreCalculator`

---

### 1.4 Resume BC (기존 — 현행 유지)

**역할**: 이력서 업로드/공유, 교육/포트폴리오 관리, 영역별 피드백, AI 피드백
**성숙도**: 기존 Hex Architecture
**전략**: 현행 유지 — Sprint 4 이후 디자인 시스템 적용만

**핵심 도메인 객체**
- `Resume` (Aggregate Root): 이력서 메타데이터 + 파일 참조
- `Education`, `Portfolio` (Entity)
- `Feedback`, `AiFeedback` (Entity)

---

## 2. Context Map

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            CONTEXT MAP                                      │
│                                                                             │
│  ┌──────────────────┐        Customer-Supplier        ┌──────────────────┐  │
│  │                  │ ──────────────────────────────▶ │                  │  │
│  │    User BC       │   (User provides profile for    │  JobSearch BC    │  │
│  │                  │    search personalization)      │                  │  │
│  │  [Existing Hex]  │ ◀────────────────────────────── │  [NEW — Core]    │  │
│  └────────┬─────────┘                                 └────────┬─────────┘  │
│           │                                                    │            │
│           │  Customer-Supplier                                 │            │
│           │  (User provides profile                            │ Published  │
│           │   for matching)                                    │ Language   │
│           │                                                    │ (Domain    │
│           ▼                                                    │ Events)    │
│  ┌──────────────────┐                                          │            │
│  │                  │ ◀────────────────────────────────────────┘            │
│  │ Recommendation   │   JobPostingCreatedEvent                              │
│  │      BC          │   JobPostingExpiredEvent                              │
│  │                  │                                                       │
│  │  [NEW — Core]    │                                                       │
│  └──────────────────┘                                                       │
│                                                                             │
│  ┌──────────────────┐        Shared Kernel           ┌──────────────────┐  │
│  │                  │ ◀────────────────────────────▶  │                  │  │
│  │   Resume BC      │   (User reference — userId FK)  │    User BC       │  │
│  │                  │                                 │                  │  │
│  │  [Existing Hex]  │                                 │  [Existing Hex]  │  │
│  └──────────────────┘                                 └──────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.1 관계 상세

| 관계 | 업스트림 (Supplier) | 다운스트림 (Customer) | 패턴 | 통합 방식 |
|------|-------------------|---------------------|------|----------|
| User ↔ JobSearch | User BC | JobSearch BC | Customer-Supplier | Anti-Corruption Layer (UserProfileDto 변환) |
| User ↔ Recommendation | User BC | Recommendation BC | Customer-Supplier | Anti-Corruption Layer (UserProfileDto 변환) |
| JobSearch ↔ Recommendation | JobSearch BC | Recommendation BC | Published Language | Domain Events (Spring ApplicationEvent) |
| Resume ↔ User | User BC | Resume BC | Shared Kernel | userId FK (Long) 직접 참조 |

---

## 3. Aggregate Design

### 3.1 JobPosting Aggregate (JobSearch BC)

```
JobPosting (Aggregate Root)
├── id: Long (PK)
├── externalId: String              # 원본 플랫폼 공고 ID
├── source: JobSource (Enum)        # WANTED, SARAMIN, JOBKOREA, LINKEDIN, ROCKETPUNCH
├── companyName: String
├── title: String
├── description: Text               # OpenSearch 인덱싱 대상
├── position: Position (Enum)       # BACKEND, FRONTEND, FULLSTACK, DATA, DEVOPS, ...
├── experienceLevel: ExperienceLevel (Enum)  # INTERN, JUNIOR, MID, SENIOR, LEAD
├── requiredSkills: List<String>
├── preferredSkills: List<String>
├── location: String
├── deadline: LocalDate             # null = 상시 채용
├── deadlineType: DeadlineType (Enum)  # ROLLING, FIXED, UNTIL_FILLED
├── status: JobStatus (Enum)        # ACTIVE, CLOSED, EXPIRED
├── viewCount: Long
├── applyClickCount: Long
├── crawledAt: LocalDateTime
├── createdAt: LocalDateTime
└── updatedAt: LocalDateTime
│
├── VO: SalaryRange (embedded)
│   ├── min: Integer
│   ├── max: Integer
│   └── currency: String            # "KRW", "USD"
│
├── VO: SourceInfo (embedded)
│   ├── source: JobSource (Enum)
│   ├── sourceUrl: String           # 원본 공고 URL (리다이렉트 대상)
│   └── externalId: String
│
└── VO: CompanyInfo (embedded)
    ├── name: String
    └── size: CompanySize (Enum)    # STARTUP, SMB, ENTERPRISE
```

**Invariants (불변 조건)**
- `externalId + source` 조합은 유일 (중복 공고 방지)
- `status = EXPIRED` 후 `status = ACTIVE` 전환 불가 (상태 머신)
- `viewCount`, `applyClickCount` 는 음수 불가

**Domain Events**
```
JobPostingCreatedEvent
├── jobPostingId: Long
├── title: String
├── source: JobSource
├── requiredSkills: List<String>
├── position: Position
├── experienceLevel: ExperienceLevel
└── occurredAt: LocalDateTime

JobPostingUpdatedEvent
├── jobPostingId: Long
├── changedFields: Set<String>      # 변경된 필드 이름 목록
└── occurredAt: LocalDateTime

JobPostingExpiredEvent
├── jobPostingId: Long
├── expiredAt: LocalDateTime
└── occurredAt: LocalDateTime
```

---

### 3.2 Recommendation Aggregate (Recommendation BC)

```
Recommendation (Aggregate Root)
├── id: Long (PK)
├── userId: Long                    # User BC 참조 (FK — Cross-BC)
├── jobPostingId: Long              # JobSearch BC 참조 (FK — Cross-BC)
├── isViewed: Boolean
├── createdAt: LocalDateTime
└── updatedAt: LocalDateTime
│
└── VO: MatchScore (embedded)
    ├── value: Double               # 0.0 ~ 1.0 (최종 가중 합산 점수)
    └── breakdown: Map<String, Double>
        # {
        #   "skillOverlap": 0.40,    # 기술스택 일치도 (w=0.40)
        #   "experienceMatch": 0.25, # 경력 수준 매칭 (w=0.25)
        #   "positionMatch": 0.20,   # 포지션 일치 (w=0.20)
        #   "locationMatch": 0.10,   # 지역 선호 (w=0.10)
        #   "recencyBonus": 0.05     # 공고 최신성 (w=0.05)
        # }
```

**매칭 점수 산출 공식**
```
matchScore = 0.40 * skillOverlap
           + 0.25 * experienceMatch
           + 0.20 * positionMatch
           + 0.10 * locationMatch
           + 0.05 * recencyBonus

# skillOverlap = |userSkills ∩ requiredSkills| / |requiredSkills|
# experienceMatch: exact=1.0, adjacent=0.5, miss=0.0
# 가중치는 application.yml로 튜닝 가능 (A/B 테스트)
```

**Match Reasons** (UI 표시용, MatchScore breakdown에서 도출)
- `"기술스택 4/5 일치"` — skillOverlap >= 0.8
- `"경력 수준 적합"` — experienceMatch == 1.0
- `"포지션 일치"` — positionMatch == 1.0
- `"선호 지역"` — locationMatch == 1.0
- `"최신 공고"` — recencyBonus >= 0.8

**Invariants**
- `matchScore.value` 범위: 0.0 ~ 1.0
- `userId + jobPostingId` 조합은 유일 (사용자당 공고당 추천 1개)

---

### 3.3 UserEvent (Standalone Entity — Event Sourcing Lite)

> Aggregate Root 아님 — 독립적으로 저장되는 이벤트 로그 엔티티.
> 불변(Immutable) 레코드 — 생성 후 수정 없음.

```
UserEvent (Standalone Entity)
├── id: Long (PK)
├── userId: Long                    # User BC 참조
├── jobPostingId: Long (nullable)   # JobSearch BC 참조. SEARCH 이벤트는 null 가능
├── eventType: UserEventType (Enum) # VIEW, BOOKMARK, APPLY_CLICK, SEARCH, SKIP
├── metadata: Map<String, Object>   # 부가 정보 (JSON 직렬화)
│   # VIEW: { "durationSeconds": 45, "source": "recommendation_feed" }
│   # SEARCH: { "keyword": "백엔드", "filters": { "position": "BACKEND" } }
│   # APPLY_CLICK: { "sourceUrl": "https://...", "referrer": "job_detail" }
│   # SKIP: { "recommendationId": 123 }
└── createdAt: LocalDateTime        # 이벤트 발생 시각 (불변)
```

**이벤트 타입 시그널 강도**
| EventType | 신호 강도 | 설명 |
|-----------|----------|------|
| `VIEW` | 약 | 공고 상세 열람 |
| `BOOKMARK` | 강 | 공고 북마크 저장 |
| `APPLY_CLICK` | 최강 | "지원하기" 클릭 → 원본 리다이렉트 |
| `SEARCH` | 관심 분야 | 검색 키워드/필터 행동 |
| `SKIP` | 부정 시그널 | 추천 공고 노출 후 미클릭 |

---

## 4. Package Structure (예정)

```
backend/src/main/java/com/techeer/backend/
├── api/
│   ├── user/                       # User BC (기존 Hex Architecture)
│   │   ├── adapter/
│   │   │   ├── in/web/
│   │   │   └── out/persistence/
│   │   ├── application/
│   │   │   ├── port/in/
│   │   │   └── port/out/
│   │   └── domain/
│   │       ├── User.java
│   │       └── UserProfile.java    # Sprint 3 확장 (BE-3.3)
│   │
│   ├── job/                        # JobSearch BC (신규 확장)
│   │   ├── adapter/
│   │   │   ├── in/web/
│   │   │   ├── out/persistence/
│   │   │   └── out/search/         # OpenSearch Adapter
│   │   ├── application/
│   │   │   ├── port/in/
│   │   │   └── port/out/
│   │   ├── domain/
│   │   │   ├── JobPosting.java     # Aggregate Root
│   │   │   ├── vo/
│   │   │   │   ├── SalaryRange.java
│   │   │   │   ├── SourceInfo.java
│   │   │   │   └── CompanyInfo.java
│   │   │   └── event/
│   │   │       ├── JobPostingCreatedEvent.java
│   │   │       ├── JobPostingUpdatedEvent.java
│   │   │       └── JobPostingExpiredEvent.java
│   │   └── infrastructure/
│   │       └── crawler/            # Strategy Pattern
│   │           ├── JobCrawler.java (interface)
│   │           ├── WantedCrawler.java
│   │           └── SaraminCrawler.java
│   │
│   ├── recommendation/             # Recommendation BC (신규)
│   │   ├── adapter/
│   │   │   ├── in/web/
│   │   │   └── out/persistence/
│   │   ├── application/
│   │   │   ├── port/in/
│   │   │   └── port/out/
│   │   └── domain/
│   │       ├── Recommendation.java # Aggregate Root
│   │       ├── UserEvent.java      # Standalone Entity
│   │       └── vo/
│   │           └── MatchScore.java
│   │
│   └── resume/                     # Resume BC (기존 유지)
│       ├── document/
│       ├── education/
│       ├── portfolio/
│       ├── feedback/
│       └── aifeedback/
│
├── global/                         # Cross-cutting (기존 유지)
│   ├── config/
│   ├── jwt/
│   ├── oauth/
│   └── exception/
│
└── infra/                          # Infrastructure (GCS로 전환 예정)
    ├── gcs/                        # Sprint 1: GCP Cloud Storage
    └── opensearch/                 # Sprint 1: OpenSearch Client
```

---

## 5. Ubiquitous Language Glossary

| 영문 용어 | 한국어 | 설명 |
|----------|--------|------|
| **JobPosting** | 채용 공고 | 외부 플랫폼에서 수집된 채용 정보. 자체 지원 시스템 없이 원본 리다이렉트 |
| **Source** | 출처 | 채용 공고가 수집된 원본 플랫폼 (원티드, 사람인 등) |
| **ExternalId** | 외부 ID | 원본 플랫폼에서 부여한 공고 고유 식별자 |
| **SourceUrl** | 원본 URL | "지원하기" 클릭 시 리다이렉트되는 원본 공고 주소 |
| **Crawling** | 크롤링 | 외부 플랫폼에서 채용 공고를 자동 수집하는 행위 |
| **Indexing** | 인덱싱 | 채용 공고를 OpenSearch에 색인하는 행위 |
| **MatchScore** | 매칭 점수 | 사용자 프로필과 채용 공고 간 적합도 (0.0~1.0) |
| **Recommendation** | 추천 | 특정 사용자에게 제안되는 채용 공고 (MatchScore 기준 Top-N) |
| **UserEvent** | 사용자 행동 이벤트 | VIEW, BOOKMARK, APPLY_CLICK 등 사용자의 공고 상호작용 기록 |
| **SkillOverlap** | 기술 스택 일치도 | 사용자 기술과 공고 필수 기술의 교집합 비율 |
| **ExperienceMatch** | 경력 매칭 | 사용자 경력 수준이 공고 요구 경력과 일치하는 정도 |
| **Aggregator** | 어그리게이터 | 여러 출처의 채용 공고를 통합 제공하는 플랫폼 (직행 모델) |
| **Apply Click** | 지원 클릭 | "지원하기" 버튼 클릭 — 원본 사이트로 리다이렉트되는 행위 |
| **ProfileCompleteness** | 프로필 완성도 | 추천 기능 활성화를 위한 필수 프로필 항목 입력 비율 (0.0~1.0) |
| **Cold Start** | 콜드 스타트 | 프로필 미완성 사용자 — 인기/최신 공고 폴백 상태 |
| **Bounded Context** | 바운디드 컨텍스트 | 특정 도메인 모델과 용어가 일관되게 적용되는 명시적 경계 |
| **Aggregate** | 애그리게이트 | 단일 트랜잭션 단위로 처리되는 연관 객체 클러스터 |
| **Domain Event** | 도메인 이벤트 | 도메인에서 발생한 의미 있는 사실 (과거형 명명, 불변) |
| **Anti-Corruption Layer** | 안티 부패 레이어 | 다른 BC의 모델이 현재 BC의 도메인 모델을 오염시키지 않도록 변환하는 레이어 |
| **Published Language** | 공개 언어 | BC 간 통신을 위해 공개적으로 정의된 프로토콜 (Domain Events) |
| **Shared Kernel** | 공유 커널 | 두 BC가 공동으로 소유하고 공유하는 코드/모델의 부분집합 |

---

## 6. Integration Patterns

### 6.1 User BC → JobSearch BC (Anti-Corruption Layer)

```
UserProfileDto (ACL — JobSearch BC 전용)
├── userId: Long
├── skills: List<String>
├── desiredPosition: String
├── experienceLevel: String
└── preferredLocations: List<String>
```

JobSearch BC는 User BC의 `User` 엔티티를 직접 참조하지 않음. `UserProfileDto`로 변환하여 사용.

### 6.2 JobSearch BC → Recommendation BC (Domain Events via Spring ApplicationEvent)

```java
// JobSearch BC publishes:
@ApplicationEvent
class JobPostingCreatedEvent {
    Long jobPostingId;
    String title;
    JobSource source;
    List<String> requiredSkills;
    Position position;
    ExperienceLevel experienceLevel;
    LocalDateTime occurredAt;
}

// Recommendation BC consumes:
@EventListener
class JobPostingEventHandler {
    void handleJobPostingCreated(JobPostingCreatedEvent event);
    void handleJobPostingExpired(JobPostingExpiredEvent event);
}
```

### 6.3 Cross-BC Identity References

BC 간 참조는 ID(Long)만 사용. 엔티티 직접 참조 금지.

| 참조 | 방식 | 이유 |
|------|------|------|
| `Recommendation.userId` | `Long` (userId만) | User BC 분리 유지 |
| `Recommendation.jobPostingId` | `Long` (jobPostingId만) | JobSearch BC 분리 유지 |
| `UserEvent.userId` | `Long` (userId만) | User BC 분리 유지 |
| `UserEvent.jobPostingId` | `Long` (nullable) | JobSearch BC 분리 유지 |

---

## 7. 변경 이력

| 날짜 | 내용 |
|------|------|
| 2026-04-01 | Sprint 0b (BE-0.4) 초안 작성 |
