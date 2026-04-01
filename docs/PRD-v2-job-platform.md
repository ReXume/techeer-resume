# PRD v2: Techeer Resume → 채용 플랫폼 확장

> **Status**: Draft v2
> **Author**: sanghun
> **Created**: 2026-04-01
> **Updated**: 2026-04-01
> **Branch**: `docs/prd-v2-job-platform`

---

## 1. 배경 및 목적

### 현재 상태 (As-Is)
- 이력서 업로드/공유/피드백 플랫폼
- AWS S3 기반 파일 저장
- OpenAI GPT 기반 AI 피드백
- Google/GitHub OAuth2 인증

### 목표 상태 (To-Be)
- **채용 공고 어그리게이터**: 직행(zighang) 모델 — 외부 채용 사이트 공고를 크롤링하여 통합 탐색, "지원하기" 클릭 시 원본 사이트로 리다이렉트
- **개인화 추천 시스템**: 규칙 기반 매칭 (MVP) → 행동 데이터 축적 후 ML 기반 확장
- **Agentic AI 서비스**: 후순위 — 이력서 분석/공고 매칭/지원 전략 자동화 (Phase 3)
- **클라우드 마이그레이션**: AWS → GCP 전면 전환 (Phase 1부터 즉시)
- **검색 고도화**: OpenSearch Docker 자체 호스팅 + Nori 한국어 형태소 분석

### 레퍼런스 플랫폼
- **직행 (zighang.com)**: 1,000+ 채용 사이트 크롤링 어그리게이터, 원본 리다이렉트 방식, 출처(source) 필터 UI, LLM + 벡터 검색 개인화
- **인디스워크 (inthiswork.com)**: 큐레이션 기반 공고 수집, 원본 리다이렉트, 콘텐츠(커리어 팁/영상) 결합

---

## 2. 핵심 기능 요구사항

### 2.1 채용 공고 수집 및 관리

> **핵심 원칙**: 자체 지원 시스템 없음 — 공고 발견(discovery) + 원본 사이트 리다이렉트 모델

| 항목 | 상세 |
|------|------|
| **공고 수집** | 외부 채용 플랫폼 크롤링 + 공식 API 연동 (API 우선, 크롤링 보조) |
| **수집 대상 (MVP)** | 원티드(Wanted), 사람인(Saramin) — 2개 플랫폼으로 시작 |
| **수집 대상 (확장)** | 잡코리아, LinkedIn, 로켓펀치, 기업 채용 페이지 직접 크롤링 |
| **공고 정규화** | 통일 스키마: 회사명, 포지션, 기술스택, 경력, 마감일, 출처, 원본 URL |
| **공고 탐색** | 다중 필터: 포지션/기술스택/경력/지역/회사 규모/출처 플랫폼/마감 유형 |
| **지원하기** | **원본 사이트로 리다이렉트** — 클릭 이벤트만 기록 (지원 트래킹용) |
| **출처 표시** | 공고 카드에 출처 뱃지 표시 (직행의 "출처" 필터 패턴) — 신뢰 시그널 |
| **공고 상태 관리** | 주기적 크롤링으로 마감/수정/삭제된 공고 자동 반영 |
| **중복 감지** | `externalId + source` 복합 키로 중복 공고 방지 |

### 2.2 OpenSearch 기반 검색 (Docker 자체 호스팅)

| 항목 | 상세 |
|------|------|
| **호스팅** | GCP Compute Engine 위 Docker 컨테이너로 자체 호스팅 |
| **전문 검색** | 채용 공고 제목, 본문, 기술스택에 대한 Full-text search |
| **한국어 형태소 분석** | Nori 플러그인 기반 한국어 토크나이저 적용 |
| **자동 완성** | 검색어 입력 시 자동 완성 (회사명, 기술스택, 포지션) — Completion Suggester |
| **유사 공고** | `more_like_this` 쿼리 — 현재 공고와 유사한 공고 추천 |
| **개인화 랭킹** | `function_score` 쿼리 — 사용자 프로필 기반 검색 결과 부스팅 |
| **필터/패싯** | 기술스택, 경력, 지역, 회사 규모, 출처 플랫폼 등 Aggregation 기반 패싯 |
| **DB 동기화** | MySQL → OpenSearch 배치 동기화 (Spring Batch, 주기: 5분) |
| **캐싱** | 인기 검색 결과 Redis 캐싱 (TTL: 10분) |

### 2.3 개인화 추천 시스템

#### MVP (Phase 2): 규칙 기반 가중치 매칭

> LinkedIn, Indeed 모두 MVP에서 규칙 기반으로 시작 — ML은 행동 데이터 축적 후 도입

**매칭 점수 산출 공식**:
```
matchScore = w1 * skillOverlap       # 0.40 — 기술스택 일치도
           + w2 * experienceMatch    # 0.25 — 경력 수준 매칭
           + w3 * positionMatch      # 0.20 — 포지션 일치
           + w4 * locationMatch      # 0.10 — 지역 선호
           + w5 * recencyBonus       # 0.05 — 공고 최신성

# skillOverlap = |userSkills ∩ requiredSkills| / |requiredSkills|
# experienceMatch = exact(1.0) / adjacent(0.5) / miss(0.0)
# 가중치는 설정 파일로 튜닝 가능
```

| 항목 | 상세 |
|------|------|
| **프로필 완성도 게이트** | 기술스택 + 포지션 + 경력 미입력 시 인기 공고 폴백 |
| **배치 점수 산출** | Spring Batch 야간 Job — 전체 (user, active_job) 쌍 점수 계산 |
| **Top-N 저장** | 사용자당 상위 50개 추천을 `Recommendation` 테이블에 저장 |
| **Redis 캐싱** | 추천 목록 캐싱 (TTL: 4시간) |
| **추천 피드** | 메인 페이지에 개인화 추천 공고 피드 |
| **매칭 사유 표시** | "기술스택 3개 일치", "경력 수준 적합" 등 투명한 사유 제공 |

#### 성숙기 (Phase 3+): ML 기반 확장

| 단계 | 조건 | 방식 |
|------|------|------|
| **협업 필터링** | 활성 사용자 500+ & 행동 데이터 축적 | MinHash 기반 유사 사용자 → implicit (ALS) |
| **임베딩 기반** | 공고 10,000+ | sentence-transformers → OpenSearch kNN 인덱스 |
| **하이브리드 리랭킹** | 위 두 데이터 확보 시 | `finalScore = 0.6 * contentScore + 0.4 * cfScore` |

#### 행동 이벤트 수집 (Phase 1부터 즉시)

> ML 학습 데이터 확보를 위해 Phase 1부터 이벤트 로깅 시작

| 이벤트 | 설명 | 시그널 |
|--------|------|--------|
| `VIEW` | 공고 상세 열람 | 관심 (약) |
| `BOOKMARK` | 공고 북마크 | 관심 (강) |
| `APPLY_CLICK` | "지원하기" 클릭 (리다이렉트) | 관심 (최강) |
| `SEARCH` | 검색 키워드/필터 | 관심 분야 |
| `SKIP` | 추천 공고 무시 (노출 후 미클릭) | 부정 시그널 |

### 2.4 Agentic AI 서비스 (Phase 3 — 후순위)

| 항목 | 상세 |
|------|------|
| **이력서 분석 에이전트** | 이력서 강점/약점 분석 + 개선 제안 |
| **공고 매칭 에이전트** | 이력서 기반 최적 공고 자동 탐색 + 적합도 설명 |
| **지원 전략 에이전트** | 공고별 맞춤 자기소개서 초안, 면접 예상 질문 생성 |
| **커리어 코칭 에이전트** | 기술 트렌드 분석 + 스킬 갭 분석 + 학습 로드맵 제안 |

### 2.5 클라우드 마이그레이션 (AWS → GCP, Phase 1부터 즉시)

| AS-IS (AWS) | TO-BE (GCP) | 비고 |
|-------------|-------------|------|
| S3 | **Cloud Storage** | PDF 파일 저장 |
| EC2 / Docker Compose | **Cloud Run** | 애플리케이션 배포 (컨테이너) |
| RDS (MySQL) | **Cloud SQL (MySQL 8.0)** | RDB |
| ElastiCache | **Memorystore (Redis)** | 캐시/세션/추천 캐싱 |
| - | **Compute Engine + Docker** | OpenSearch 자체 호스팅 |
| - | **Cloud Pub/Sub** | 크롤링 스케줄링 + 이벤트 메시지 큐 |
| ECR | **Artifact Registry** | 컨테이너 이미지 |
| - | **Cloud Build + GitHub Actions** | CI/CD |
| CloudWatch | **Cloud Monitoring + Cloud Logging** | 모니터링 |
| - | **Secret Manager** | 시크릿 관리 |
| - | **Cloud Scheduler** | 크롤러 주기 실행 트리거 |

---

## 3. 시스템 아키텍처 (High-Level)

```
┌─────────────────┐     ┌───────────────────────────────────────────────────┐
│  Frontend       │────▶│              Cloud Run / Traefik                  │
│  (React + Vite) │     └──────┬──────────┬──────────┬──────────┬──────────┘
└─────────────────┘            │          │          │          │
                    ┌──────────▼──┐ ┌─────▼──────┐ ┌▼────────┐ ┌▼──────────┐
                    │ Resume API  │ │ Job API    │ │ Search  │ │ Recommend │
                    │ (Spring)    │ │ (Spring)   │ │ API     │ │ API       │
                    └──────┬──────┘ └─────┬──────┘ └────┬────┘ └─────┬─────┘
                           │              │             │             │
         ┌─────────────────▼──────────────▼─────────────▼─────────────▼──┐
         │                         Data Layer                            │
         │  ┌────────────┐  ┌─────────────────┐  ┌───────────────────┐   │
         │  │ Cloud SQL  │  │ OpenSearch       │  │ Memorystore       │   │
         │  │ (MySQL)    │  │ (Docker/GCE)     │  │ (Redis)           │   │
         │  │            │  │ + Nori Plugin    │  │                   │   │
         │  └────────────┘  └─────────────────┘  └───────────────────┘   │
         └───────────────────────────────────────────────────────────────┘
                           │
         ┌─────────────────▼─────────────────────┐
         │         Async / Batch Layer            │
         │  ┌──────────────┐  ┌────────────────┐  │
         │  │ Job Crawler  │  │ Batch Scoring  │  │
         │  │ (Scheduler   │  │ (Spring Batch) │  │
         │  │  + Pub/Sub)  │  │                │  │
         │  └──────────────┘  └────────────────┘  │
         └────────────────────────────────────────┘
                           │
         ┌─────────────────▼─────────────────────┐
         │         Event Logging                  │
         │  ┌──────────────────────────────────┐  │
         │  │ User Behavior Events (Pub/Sub)   │  │
         │  │ VIEW, BOOKMARK, APPLY_CLICK, ... │  │
         │  └──────────────────────────────────┘  │
         └────────────────────────────────────────┘
```

---

## 4. 데이터 모델 (주요 엔티티)

### 4.1 채용 공고 (JobPosting)
```
JobPosting
├── id: Long (PK)
├── externalId: String              # 원본 플랫폼 공고 ID
├── source: Enum                    # WANTED, SARAMIN, JOBKOREA, LINKEDIN, ROCKETPUNCH, ...
├── sourceUrl: String               # 원본 공고 URL (지원하기 리다이렉트 대상)
├── companyName: String
├── companySize: Enum               # STARTUP, SMB, ENTERPRISE
├── title: String
├── description: Text               # 공고 본문 (OpenSearch 인덱싱 대상)
├── position: Enum                  # BACKEND, FRONTEND, FULLSTACK, DATA, DEVOPS, ...
├── experienceLevel: Enum           # INTERN, JUNIOR, MID, SENIOR, LEAD
├── requiredSkills: List<Tag>
├── preferredSkills: List<Tag>
├── location: String
├── salary: SalaryRange (embedded)  # min, max, currency
├── deadline: LocalDate             # null = 상시 채용
├── deadlineType: Enum              # ROLLING, FIXED, UNTIL_FILLED
├── status: Enum                    # ACTIVE, CLOSED, EXPIRED
├── viewCount: Long                 # 조회수
├── applyClickCount: Long           # 지원 클릭수
├── crawledAt: LocalDateTime        # 최종 크롤링 시각
├── createdAt: LocalDateTime
└── updatedAt: LocalDateTime
```

### 4.2 사용자 행동 이벤트 (UserEvent)
```
UserEvent
├── id: Long (PK)
├── userId: Long (FK)
├── jobPostingId: Long (FK)
├── eventType: Enum                 # VIEW, BOOKMARK, APPLY_CLICK, SEARCH, SKIP
├── metadata: JSON                  # 검색어, 필터 조건 등 부가 정보
├── createdAt: LocalDateTime
```

### 4.3 추천 (Recommendation)
```
Recommendation
├── id: Long (PK)
├── userId: Long (FK)
├── jobPostingId: Long (FK)
├── matchScore: Double              # 0.0 ~ 1.0
├── matchReasons: List<String>      # ["기술스택 4/5 일치", "경력 수준 적합"]
├── isViewed: Boolean
├── createdAt: LocalDateTime
└── updatedAt: LocalDateTime
```

### 4.4 사용자 프로필 확장 (UserProfile — 기존 User 확장)
```
UserProfile (extends existing User)
├── desiredPosition: Enum           # 희망 포지션
├── experienceLevel: Enum           # 현재 경력 수준
├── skills: List<Tag>               # 보유 기술스택
├── preferredLocations: List<String> # 선호 지역
├── preferredCompanySize: List<Enum> # 선호 회사 규모
├── salaryExpectation: SalaryRange  # 희망 연봉 범위
├── openToRemote: Boolean           # 원격 근무 관심
└── profileCompleteness: Double     # 프로필 완성도 (0.0 ~ 1.0)
```

---

## 5. 구현 단계 (Phased Rollout)

### Phase 1: 채용 공고 + 검색 + GCP 전환 (MVP)
- [ ] GCP 인프라 셋업 (Cloud Run, Cloud SQL, Memorystore, Cloud Storage)
- [ ] AWS S3 → GCP Cloud Storage 마이그레이션
- [ ] OpenSearch Docker 자체 호스팅 (GCE + Nori 플러그인)
- [ ] 채용 공고 데이터 모델 + CRUD API
- [ ] 크롤러 구현 (원티드, 사람인 — API 우선, 크롤링 보조)
- [ ] Cloud Scheduler + Pub/Sub 기반 크롤링 스케줄링
- [ ] OpenSearch 인덱싱 + 전문 검색 + 패싯 필터
- [ ] 채용 공고 목록/상세/검색 프론트엔드
- [ ] "지원하기" → 원본 사이트 리다이렉트 + 클릭 이벤트 로깅
- [ ] 출처 뱃지 UI + 출처 필터
- [ ] 사용자 행동 이벤트 로깅 시작 (VIEW, BOOKMARK, APPLY_CLICK)
- [ ] CI/CD 파이프라인 GCP 대응 (Cloud Build + GitHub Actions)

### Phase 2: 추천 시스템
- [ ] 사용자 프로필 확장 (기술스택, 희망 포지션, 경력, 선호 지역)
- [ ] 규칙 기반 매칭 점수 산출 (Spring Batch 야간 Job)
- [ ] 추천 피드 API + 프론트엔드
- [ ] OpenSearch `function_score` 기반 개인화 검색 랭킹
- [ ] OpenSearch `more_like_this` 유사 공고 추천
- [ ] 자동 완성 (Completion Suggester)
- [ ] 크롤링 대상 확장 (잡코리아, 로켓펀치 등)

### Phase 3: Agentic AI + ML 추천 (후순위)
- [ ] 협업 필터링 도입 (활성 사용자 500+ 도달 시)
- [ ] 임베딩 기반 매칭 (sentence-transformers + OpenSearch kNN)
- [ ] AI 에이전트 서비스 (이력서 분석/공고 매칭/지원 전략)
- [ ] 알림 시스템 (Pub/Sub + 이메일/인앱)

---

## 6. 기술적 고려사항

### OpenSearch (Docker 자체 호스팅)
- **배포**: GCP Compute Engine (e2-standard-4 이상) + Docker Compose
- **인덱스 설계**: `job_postings` 인덱스 + Nori 형태소 분석기 + ngram 자동완성 필드
- **동기화**: Spring Batch 5분 주기 증분 동기화 (`updatedAt` 기준)
- **성능**: Redis 캐싱 (인기 검색 TTL 10분), 스크롤 기반 Pagination
- **운영**: 단일 노드로 시작, 공고 100,000+ 시 클러스터 확장 검토

### 크롤러
- **합법성**: 공식 API 최우선, robots.txt 준수, 적절한 크롤링 간격 (5초+)
- **API 연동**: 원티드 Open API, 사람인 API — 파트너십/키 발급 필요
- **스케줄링**: Cloud Scheduler → Pub/Sub → Cloud Run Job (1시간 주기)
- **정합성**: `externalId + source` 복합 유니크 키, 마감 공고 상태 자동 전환
- **확장**: 새 플랫폼 추가 시 크롤러 인터페이스(Strategy 패턴)로 플러그인 방식 확장

### 추천 시스템
- **MVP**: 규칙 기반 가중치 점수 — 업계 검증된 접근 (LinkedIn/Indeed 초기 모델)
- **가중치 튜닝**: `application.yml` 설정으로 A/B 테스트 가능
- **콜드 스타트**: 프로필 미완성 사용자 → 인기 공고/최신 공고 폴백
- **데이터 준비**: Phase 1부터 행동 이벤트 적재 → Phase 3 ML 학습 데이터 확보

### Agentic AI (후순위)
- **LLM**: OpenAI GPT / Google Gemini (GCP 환경에서 Gemini 우선 검토)
- **오케스트레이션**: LangChain/LangGraph 또는 자체 에이전트 프레임워크
- **비용 관리**: 토큰 사용량 모니터링 + 응답 캐싱

---

## 7. 결정 사항 (Decisions Made)

| # | 항목 | 결정 | 근거 |
|---|------|------|------|
| 1 | 공고 수집 모델 | 직행 스타일 어그리게이터 — 크롤링/API 수집 + 원본 리다이렉트 | 자체 지원 시스템 불필요, 법적 리스크 최소화 |
| 2 | MVP 크롤링 대상 | 원티드 + 사람인 (2개) | API 제공 여부 + 시장 커버리지 |
| 3 | OpenSearch 호스팅 | GCP Compute Engine + Docker 자체 호스팅 | 비용 절감 + 운영 자율성 |
| 4 | GCP 전환 시점 | Phase 1부터 즉시 전면 전환 | 이중 인프라 운영 비용 회피 |
| 5 | 추천 알고리즘 MVP | 규칙 기반 가중치 매칭 | 업계 표준 MVP 접근, ML은 데이터 축적 후 |
| 6 | Agentic AI | Phase 3 후순위 | 핵심 가치(공고 탐색/추천) 우선 |

## 8. 미결 사항 (Open Questions)

1. **원티드/사람인 API 접근**: 공식 API 키 발급 절차 확인 필요 — 파트너십 or 오픈 API?
2. **크롤링 법적 검토**: 공식 API 없는 플랫폼 크롤링 시 이용약관 검토 필요
3. **사용자 지원 이력 관리**: 플랫폼 내에서 "지원한 공고" 트래킹 기능 범위 (클릭 기록만 vs 상태 관리)
4. **모바일 대응**: 반응형 웹으로 충분한지, PWA 검토 여부
5. **Agentic AI LLM 선택**: OpenAI 유지 vs Gemini 전환 vs 혼용 — Phase 3에서 결정
6. **OpenSearch 클러스터링 시점**: 공고 데이터 규모에 따른 확장 기준 수립

---

## 변경 이력

| 날짜 | 내용 |
|------|------|
| 2026-04-01 | 초안 작성 |
| 2026-04-01 | v2: 직행 모델 반영, OpenSearch Docker 자체 호스팅, GCP 즉시 전환, 규칙 기반 추천 MVP 확정, 행동 이벤트 수집 추가, Agentic AI 후순위 조정 |
