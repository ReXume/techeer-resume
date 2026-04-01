# Techeer Resume

채용 공고 검색 및 추천 플랫폼입니다. 여러 채용 플랫폼의 공고를 통합 탐색하고, 이력서 프로필 기반 개인화 추천을 제공합니다. "지원하기" 클릭 시 원본 채용 사이트로 리다이렉트됩니다.

> **v2 전환 중**: 이력서 공유/피드백 플랫폼 → 채용 공고 어그리게이터 + 추천 플랫폼 (직행 모델)
> 상세 계획: [`docs/PRD-v2-job-platform.md`](docs/PRD-v2-job-platform.md) | [`docs/IMPLEMENTATION_PLAN.md`](docs/IMPLEMENTATION_PLAN.md)

## 주요 기능

### 채용 공고 (v2 — 개발 중)

- **공고 수집/통합**: 원티드, 사람인 등 외부 플랫폼 크롤링 + API 연동
- **OpenSearch 검색**: 전문 검색, 한국어 형태소 분석(Nori), 자동완성, 패싯 필터
- **개인화 추천**: 기술스택/경력/포지션 기반 규칙 가중치 매칭 점수 산출
- **행동 이벤트 수집**: VIEW, BOOKMARK, APPLY_CLICK 등 ML 학습 데이터 축적
- **원본 리다이렉트**: "지원하기" 클릭 시 원본 채용 사이트로 이동 (자체 지원 없음)

### 이력서 플랫폼 (v1 — 유지)

- **이력서 업로드/관리**: PDF 이력서를 포지션, 경력, 기술 스택 메타데이터와 함께 업로드
- **이력서 탐색**: 포지션, 경력, 기술 스택 기준 필터링 및 검색
- **영역별 피드백**: PDF 위에 특정 영역을 지정하여 피드백 작성
- **AI 피드백**: OpenAI GPT를 활용한 자동 이력서 분석
- **북마크**: 관심 있는 이력서 저장
- **소셜 로그인**: Google, GitHub OAuth2 인증

## 기술 스택

### Frontend

| 분류 | 기술 |
|------|------|
| Framework | React 18, TypeScript |
| Build | Vite 5 |
| Styling | Tailwind CSS, Radix UI, MUI (점진적 제거 예정) |
| 상태관리 | Zustand, TanStack React Query |
| PDF | react-pdf, @react-pdf-viewer |
| 모니터링 | Sentry |

### Backend

| 분류 | 기술 |
|------|------|
| Framework | Spring Boot 3.5.6, Java 21 |
| Architecture | DDD + Hexagonal (Port & Adapter) |
| Database | MySQL 8.0, Redis |
| Search | OpenSearch 2.x + Nori 한국어 형태소 분석 |
| Batch | Spring Batch 5 (크롤링 스케줄링, 추천 점수 산출) |
| ORM | JPA/Hibernate, QueryDSL |
| 인증 | Spring Security, JWT, OAuth2 |
| 파일 저장 | GCP Cloud Storage (AWS S3 → GCP 마이그레이션) |
| AI | OpenAI GPT API |
| 문서 | Swagger (SpringDoc OpenAPI) |

### Infra (GCP)

| 분류 | 기술 |
|------|------|
| 컨테이너 | Docker, Docker Compose |
| 배포 | GCP Cloud Run |
| RDB | GCP Cloud SQL (MySQL 8.0) |
| 캐시 | GCP Memorystore (Redis) |
| 파일 저장 | GCP Cloud Storage |
| 검색 엔진 | OpenSearch on GCP Compute Engine (Docker 자체 호스팅) |
| 메시지 큐 | GCP Cloud Pub/Sub (크롤링 스케줄링, 이벤트) |
| 스케줄러 | GCP Cloud Scheduler |
| 리버스 프록시 | Traefik v3 (Let's Encrypt SSL) |
| CI/CD | GitHub Actions + GCP Cloud Build |
| 모니터링 | Grafana, Prometheus, GCP Cloud Monitoring |
| 부하 테스트 | K6 |
| 시크릿 | GCP Secret Manager |

## 프로젝트 구조

```
techeer-resume/
├── backend/                  # Spring Boot API 서버
│   └── src/main/java/com/techeer/backend/
│       ├── api/
│       │   ├── job/          # JobSearch BC — 채용 공고 (v2 신규)
│       │   ├── recommendation/ # Recommendation BC — 추천 (v2 신규)
│       │   ├── user/         # User BC — 사용자/인증 (hex architecture)
│       │   ├── resume/       # Resume BC — 이력서 (기존 유지)
│       │   │   ├── document/ # 이력서 CRUD
│       │   │   ├── education/ # 학력
│       │   │   ├── portfolio/ # 포트폴리오
│       │   │   ├── feedback/ # 사용자 피드백
│       │   │   └── aifeedback/ # AI 피드백
│       │   ├── bookmark/     # 북마크
│       │   ├── tag/          # 기술스택/회사 태그
│       │   └── company/      # 회사 정보
│       ├── global/           # 설정, JWT, OAuth, 에러 처리
│       └── infra/            # GCP Cloud Storage, OpenSearch 클라이언트
├── frontend/                 # React SPA
│   └── src/
│       ├── pages/            # 페이지 컴포넌트
│       ├── components/       # 공통/기능별 컴포넌트
│       ├── api/              # API 클라이언트
│       ├── store/            # Zustand 상태 관리
│       └── utils/            # 유틸리티
├── docs/                     # 설계 문서
│   ├── PRD-v2-job-platform.md   # 제품 요구사항 문서 v2
│   ├── IMPLEMENTATION_PLAN.md   # 구현 계획 (스프린트)
│   └── domain-model.md          # DDD Context Map + Aggregate 설계
├── .claude/                  # Claude Code 설정
│   └── skills/               # 자동화 스킬
├── traefik/                  # 리버스 프록시 설정
├── k6/                       # 부하 테스트 스크립트
├── grafana-dashboard/        # Grafana 대시보드
├── seed/                     # 샘플 데이터 시딩
├── docker-compose.yml        # 프로덕션 구성
├── docker-compose.simple.yml # 로컬 개발 구성
└── docker-compose.monitor.yml # 모니터링 스택
```

## 시작하기

### Docker Compose (권장)

```bash
# 전체 서비스 실행 (MySQL, Redis, Backend, Frontend)
docker-compose -f docker-compose.simple.yml up -d

# 로그 확인
docker-compose -f docker-compose.simple.yml logs -f

# 종료
docker-compose -f docker-compose.simple.yml down
```

실행 후 접속:
- **Frontend** : http://localhost:3000
- **Backend API** : http://localhost:8080
- **Swagger UI** : http://localhost:8080/swagger-ui/index.html

### 로컬 개발

**Backend**

```bash
cd backend
./gradlew bootRun
# http://localhost:8080
```

**Frontend**

```bash
cd frontend
yarn install
yarn dev
# http://localhost:5173
```

### 환경 변수

**Backend** (`application.yml` 참조)

| 변수 | 설명 |
|------|------|
| `MYSQL_URL` | MySQL 접속 URL |
| `MYSQL_USER` / `MYSQL_ROOT_PASSWORD` | DB 인증 정보 |
| `REDIS_HOST` / `REDIS_PORT` | Redis 접속 정보 |
| `SECURITY_SECRET_KEY` | JWT 시크릿 키 |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Google OAuth2 |
| `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET` | GitHub OAuth2 |
| `GCP_PROJECT_ID` | GCP 프로젝트 ID |
| `GCP_STORAGE_BUCKET_NAME` | Cloud Storage 버킷명 |
| `OPENSEARCH_HOST` / `OPENSEARCH_PORT` | OpenSearch 접속 정보 |
| `GPT_API_KEY` | OpenAI API 키 |

**Frontend** (`.env`)

| 변수 | 설명 |
|------|------|
| `VITE_API_BASE_URL` | Backend API 기본 URL |
| `VITE_LOCAL_URI` | 로컬 Backend URI |
| `VITE_SENTRY_DSN` | Sentry DSN |

## 아키텍처

자세한 내용은 [`docs/domain-model.md`](docs/domain-model.md) 참조.

```
Frontend (React)
     │
     ▼
Cloud Run / Traefik
     │
     ├── Resume API   (이력서/피드백)
     ├── Job API      (채용 공고 CRUD + 크롤링)
     ├── Search API   (OpenSearch 검색/자동완성)
     └── Recommend API (추천 피드)
           │
           ├── Cloud SQL (MySQL)       — 원본 데이터
           ├── OpenSearch (GCE/Docker) — 검색 인덱스 (Nori)
           └── Memorystore (Redis)     — 캐시/추천 TTL
                 │
           ┌─────┴──────┐
           │ Async/Batch │
           ├── Job Crawler (Cloud Scheduler + Pub/Sub)
           └── Recommendation Scoring (Spring Batch, 야간)
```
