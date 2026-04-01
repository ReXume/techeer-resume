# Implementation Plan: Techeer Resume → Job Aggregator Platform

> **Status**: Approved (Planner-Architect-Critic Consensus)
> **Approach**: Strangler Fig (DDD-native new capabilities + demand-driven enrichment)
> **Timeline**: ~8-10 weeks (6 sprints)
> **Branch**: `docs/prd-v2-job-platform`

---

## Principles

1. **DDD-First**: 도메인 모델이 기술적 결정을 주도
2. **Preserve What Works**: global 패키지 + 기존 hex 아키텍처 유지. 동작하는 코드를 불필요하게 재작성하지 않음
3. **TDD Discipline**: Red-Green-Refactor. 테스트 인프라를 Sprint 0에서 구축
4. **Incremental Delivery**: 매 스프린트 동작하는 소프트웨어 전달
5. **Search-Native**: OpenSearch가 일급 시민

## ADR (Architecture Decision Record)

### Decision: Strangler Fig 점진적 교체

**Drivers**:
- 채용 공고 어그리게이터가 핵심 가치 → 새 기능 우선 전달
- 기존 hex 아키텍처 9개 도메인 동작 중 → 불필요한 재작성 회피
- 애자일 프로세스 → 매 스프린트 가치 전달

**Alternatives Considered**:
- Option A (Big Rewrite): 전면 재작성 → **기각** (애자일 원칙 위반, 장기간 서비스 중단)
- Option C (Skip DDD): DDD 없이 기능 추가 → **기각** (추천 엔진에 풍부한 도메인 모델 필요)

**Why Chosen**: 새 Bounded Context(JobSearch, Recommendation)를 DDD로 구축하고, 기존 도메인은 새 기능이 요구할 때만 강화. 진정한 Strangler Fig.

**Consequences**: 일시적으로 두 성숙도의 도메인 공존. 아키텍처 일관성보다 전달 속도 우선.

**Follow-ups**: 추천 엔진 완성 후 기존 도메인 강화 여부 재평가.

---

## Pre-Mortem (3 Scenarios)

| # | 시나리오 | 확률 | 영향 | 완화 전략 |
|---|---------|------|------|----------|
| 1 | **OpenSearch Nori PoC 실패** | 중 | 높 | Sprint 0a GATE. PoC = Docker + cURL로 한국어 토크나이저 검증. Java 연동은 Sprint 1. 실패 시 Elasticsearch 대안 검토 |
| 2 | **크롤러 법적/기술 차단** | 중 | 높 | API 우선. 원티드/사람인 ToS 사전 검토. 차단 시 관리자 수동 입력 UI + CSV import로 MVP 대체 |
| 3 | **GCP 비용 초과** | 낮 | 중 | 개발: Docker Compose 로컬. 프로덕션만 GCP 매니지드. 예산 알림 설정. OpenSearch는 e2-standard-2로 시작 |

---

## Test Plan

| 레벨 | 대상 | 도구 | 목표 | 근거 |
|------|------|------|------|------|
| **Unit** | Domain entities/VOs/Services | JUnit 5 + Mockito | 80% (domain layer) | 도메인 로직이 비즈니스 핵심, 현재 0% |
| **Integration** | Repository + OpenSearch + Redis | TestContainers (MySQL, Redis, OpenSearch) | 주요 흐름 100% | 검색 정확도와 데이터 정합성 검증 필수 |
| **E2E** | API 전체 흐름 | REST Docs + MockMvc | Happy path + error cases | API 계약 문서화 겸용 |
| **Frontend** | 핵심 컴포넌트 + 페이지 | Vitest + Testing Library (이미 구축됨) | 핵심 UI 70% | 검색/필터/추천 피드가 핵심 UX |
| **Observability** | 검색 지연, 크롤링 실패 | Grafana Alloy + Actuator (이미 구축됨) | 알림 설정 완료 | SLA 모니터링 |

---

## Sprint Plan

### Sprint 0a: OpenSearch PoC + 테스트 인프라 (4일)

> **GATE**: Nori 한국어 토크나이저 검증 통과해야 Sprint 1 진행

| ID | 작업 | 상세 | 완료 기준 |
|----|------|------|----------|
| BE-0.1 | OpenSearch Docker PoC | `docker-compose.yml`에 OpenSearch + Nori 추가. cURL로 한국어 형태소 분석 검증 | `"백엔드 개발자"` → `["백엔드", "개발자"]` 토큰화 확인 |
| BE-0.2 | 테스트 인프라 | `build.gradle`에 `opensearch-java`, `spring-boot-starter-batch`, `testcontainers:elasticsearch` 추가. TestContainer 베이스 클래스 작성 | OpenSearch TestContainer 기동 + 인덱스 생성 테스트 통과 |
| BE-0.3 | UserService 정리 | `updateProfileImage()` + GCS 로직을 `ProfileImageService`로 추출. `UserService`를 hex 포트(`LoadUserPort`/`SaveUserPort`)로 정렬 | UserService 250줄 이하. 기존 테스트 통과 |

### Sprint 0b: 도메인 설계 + 디자인 시스템 (4일)

**병렬 가능**: BE-0.4 ∥ FE-0.1 ∥ INFRA-0.1

| ID | 작업 | 상세 | 완료 기준 |
|----|------|------|----------|
| BE-0.4 | DDD Context Map | Resume BC ↔ JobSearch BC (NEW) ↔ Recommendation BC (NEW) ↔ User BC. Aggregate/VO/Event 정의 문서 | `docs/domain-model.md` 작성 완료 |
| FE-0.1 | 디자인 시스템 | ui-ux-pro-max 스킬로 직행/원티드 참고 디자인 시스템 생성. **결정: Tailwind 중심 + Radix UI 프리미티브. MUI 점진적 제거** | `design-system/MASTER.md` 생성 |
| INFRA-0.1 | GCP 프로필 | `application-gcp.yml` 작성 (Cloud SQL, Memorystore, GCS 설정). Secret Manager 연동 문서 | 프로필 전환으로 GCP 연결 가능 |

### Sprint 1: JobSearch Bounded Context (2주)

> 기존 Job 도메인 hex 아키텍처를 **확장** (재작성 아님)

| ID | 작업 | 상세 | 완료 기준 |
|----|------|------|----------|
| BE-1.1 | JobPosting 도메인 확장 | 크롤링 필드 추가 (`externalId`, `sourceUrl`, `crawledAt`). VO: `SalaryRange`, `SourceInfo`. 도메인 이벤트: `JobPostingCreatedEvent` | 단위 테스트 통과. 기존 CRUD 정상 |
| BE-1.2 | OpenSearch 연동 | `job_postings` 인덱스 (Nori + ngram). MySQL→OpenSearch 동기화 (Spring Batch 5분). 검색 API: full-text, 필터, 패싯. **Feature flag: `search.engine=opensearch\|jpa`** | 검색 결과가 JPA 결과와 95% 일치. 한국어 검색 동작 |
| BE-1.3 | 크롤러 MVP | Strategy 패턴 인터페이스. 원티드 API 연동. 정규화 + 중복감지 (`externalId+source` unique). @Scheduled 1시간 주기 | 원티드 공고 50건+ 수집 + OpenSearch 인덱싱 |
| BE-1.4 | 초기 데이터 로드 | 기존 JobPosting 레코드 → OpenSearch 벌크 인덱싱 스크립트 | 기존 데이터 100% 인덱싱 |
| FE-1.1 | 글로벌 레이아웃 | 직행/원티드 참고 레이아웃. 반응형 네비바. 새 디자인 시스템 적용 | 375px/768px/1024px/1440px 반응형 확인 |

### Sprint 2: 검색 + 공고 UI (2주)

**병렬 가능**: FE-2.1 ∥ FE-2.2 ∥ FE-2.3 (BE-2.1은 FE와 병렬)

| ID | 작업 | 상세 | 완료 기준 |
|----|------|------|----------|
| FE-2.1 | 공고 목록 페이지 | 출처 뱃지, 기술스택 태그, 마감일. 다중 필터 (포지션/경력/기술스택/지역/출처). 패싯 카운트. 무한 스크롤 | 필터 조합 테스트 통과. 출처 뱃지 표시 |
| FE-2.2 | 공고 상세 페이지 | 본문 + 회사 정보. **"지원하기" → 원본 사이트 리다이렉트**. `more_like_this` 유사 공고. 북마크 | 리다이렉트 동작. 유사 공고 3개+ 표시 |
| FE-2.3 | 검색 리뉴얼 | OpenSearch 전문 검색 UI. 자동완성 (debounce 300ms). 검색어 하이라이팅 | 한국어 검색 + 자동완성 동작 |
| BE-2.1 | 행동 이벤트 | `UserEvent` 엔티티 + API. VIEW/BOOKMARK/APPLY_CLICK/SEARCH/SKIP. Redis 실시간 카운팅 | 이벤트 저장 + 조회수/클릭수 정확 |

### Sprint 3: 추천 시스템 (2주)

| ID | 작업 | 상세 | 완료 기준 |
|----|------|------|----------|
| BE-3.1 | Recommendation BC | 순수 DDD 신규 Bounded Context. `Recommendation` Aggregate. 매칭 점수 산출 (가중치: skill 40%, exp 25%, pos 20%, loc 10%, recency 5%) | 단위 테스트: 점수 산출 정확도 검증 |
| BE-3.2 | Spring Batch Job | 야간 배치: (user, active_job) 매칭 → Top-50 저장. Redis 캐싱 TTL 4h | 배치 완료 + 캐시 적중 확인 |
| BE-3.3 | UserProfile 확장 | 기존 User에 프로필 필드 추가. **수요 기반 강화**: `User.updateUser()` DTO 의존성 제거 | 프로필 완성도 계산 동작 |
| BE-3.4 | 개인화 검색 | OpenSearch `function_score` + `more_like_this` | 프로필 완성 사용자의 검색 결과 순서 변경 확인 |
| FE-3.1 | 추천 피드 | 메인 페이지 개인화 추천 공고. 프로필 미완성 유도 UI | 추천 공고 표시 + 매칭 사유 확인 |
| FE-3.2 | 마이페이지 | 프로필 편집. 지원 클릭 이력. 북마크 목록 | 프로필 저장 + 이력 표시 |

### Sprint 4: 크롤러 확장 + 페이지 리뉴얼 (1-2주)

> **수요 기반 도메인 강화만**. 전면 DDD 리팩토링 아님.

| ID | 작업 | 상세 | 완료 기준 |
|----|------|------|----------|
| BE-4.1 | 사람인 크롤러 | Strategy 패턴 플러그인. API/크롤링. 멀티소스 스케줄링 | 사람인 공고 수집 + 인덱싱 |
| BE-4.2 | 수요 기반 강화 | 추천이 요구하는 VO 추출만. 빈 old-style 패키지 정리. `document/repository/`, `document/service/` 빈 디렉토리 제거 | 기존 테스트 통과 |
| FE-4.1 | 이력서/피드백 리뉴얼 | 기존 기능 유지 + 새 디자인 시스템 적용 | 기존 기능 정상 + 디자인 통일 |

### Sprint 5: GCP 배포 + 모니터링 (1주)

| ID | 작업 | 상세 | 완료 기준 |
|----|------|------|----------|
| INFRA-5.1 | GCP 배포 | Cloud Run 설정. Cloud SQL + Memorystore 연결. OpenSearch on GCE (e2-standard-2) | 헬스체크 통과. API 응답 |
| INFRA-5.2 | CI/CD | GitHub Actions에 GCP 인증 + Artifact Registry push + Cloud Run deploy 추가 | PR 머지 → 자동 배포 |
| INFRA-5.3 | 모니터링 | Grafana 대시보드: 검색 지연, 크롤링 실패 알림. K6 부하 테스트 | 알림 트리거 확인 |
| ALL-5.1 | E2E 검증 | 전체 흐름 테스트: 크롤링→인덱싱→검색→추천→리다이렉트 | E2E 테스트 통과 |

---

## Scope Decisions

### In-Scope (이번 리팩토링)
- JobSearch BC (확장 + OpenSearch)
- Recommendation BC (신규)
- User BC (프로필 확장 + hex 정렬)
- 프론트엔드 전체 UI/UX 리뉴얼
- GCP 매니지드 서비스 배포

### Out-of-Scope (현행 유지)
- Company, Bookmark, Application, Career, Skill, Document(Resume/Portfolio/Education) — 기존 hex 아키텍처 유지
- Agentic AI (Phase 3 후순위)
- 모바일 앱 (반응형 웹으로 대응)

### Coexistence Strategy (Strangler Fig)
- **검색**: Feature flag `search.engine=opensearch|jpa`. OpenSearch 장애 시 JPA 폴백
- **API 버전**: 새 엔드포인트는 `/api/v2/jobs/search`, 기존은 `/api/v1/` 유지
- **프론트엔드**: 새 페이지(공고 목록/상세/검색)는 신규 라우트. 기존 페이지는 디자인만 업데이트

---

## 변경 이력

| 날짜 | 내용 |
|------|------|
| 2026-04-01 | Planner 초안 |
| 2026-04-01 | Architect 리뷰 반영 (Sprint 4 수요 기반 전환, 테스트 인프라 Sprint 0, Sprint 0 분리) |
| 2026-04-01 | Critic 리뷰 반영 (UserService 분리 구체화, Sprint 0a 4일 확장, feature flag 추가, 수용 기준 추가) |
