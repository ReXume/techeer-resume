# Techeer Resume

이력서 공유 및 피드백 플랫폼입니다. PDF 이력서를 업로드하고, 다른 사용자의 이력서를 탐색하며, 영역별 피드백과 AI 피드백을 받을 수 있습니다.

## 주요 기능

- **이력서 업로드/관리** : PDF 이력서를 포지션, 경력, 기술 스택 등 메타데이터와 함께 업로드
- **이력서 탐색** : 포지션, 경력, 기술 스택 기준 필터링 및 검색
- **영역별 피드백** : PDF 위에 특정 영역을 지정하여 피드백 작성
- **AI 피드백** : OpenAI GPT를 활용한 자동 이력서 분석
- **북마크** : 관심 있는 이력서 저장
- **소셜 로그인** : Google, GitHub OAuth2 인증

## 기술 스택

### Frontend

| 분류 | 기술 |
|------|------|
| Framework | React 18, TypeScript |
| Build | Vite 5 |
| Styling | Tailwind CSS, MUI, Emotion |
| 상태관리 | Zustand, TanStack React Query |
| PDF | react-pdf, @react-pdf-viewer |
| 모니터링 | Sentry |

### Backend

| 분류 | 기술 |
|------|------|
| Framework | Spring Boot 3.3, Java 17 |
| Database | MySQL 8.0, Redis 6.0 |
| ORM | JPA/Hibernate, QueryDSL |
| 인증 | Spring Security, JWT, OAuth2 |
| 파일 저장 | AWS S3 |
| AI | OpenAI GPT API |
| 문서 | Swagger (SpringDoc OpenAPI) |

### Infra

| 분류 | 기술 |
|------|------|
| 컨테이너 | Docker, Docker Compose |
| 리버스 프록시 | Traefik v3 (Let's Encrypt SSL) |
| 모니터링 | Grafana, Prometheus |
| 부하 테스트 | K6 |
| CI/CD | GitHub Actions |

## 프로젝트 구조

```
techeer-resume/
├── backend/                  # Spring Boot API 서버
│   └── src/main/java/com/techeer/backend/
│       ├── api/
│       │   ├── resume/       # 이력서 CRUD
│       │   ├── feedback/     # 사용자 피드백
│       │   ├── aifeedback/   # AI 피드백
│       │   ├── bookmark/     # 북마크
│       │   ├── user/         # 사용자/인증
│       │   └── tag/          # 기술스택/회사 태그
│       ├── global/           # 설정, JWT, OAuth, 에러 처리
│       └── infra/            # AWS S3 연동
├── frontend/                 # React SPA
│   └── src/
│       ├── pages/            # 페이지 컴포넌트
│       ├── components/       # 공통/기능별 컴포넌트
│       ├── api/              # API 클라이언트
│       ├── store/            # Zustand 상태 관리
│       └── utils/            # 유틸리티
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
| `AWS_ACCESS_KEY` / `AWS_SECRET_KEY` | AWS 인증 |
| `AWS_S3_BUCKET_NAME` | S3 버킷명 |
| `GPT_API_KEY` | OpenAI API 키 |

**Frontend** (`.env`)

| 변수 | 설명 |
|------|------|
| `VITE_API_BASE_URL` | Backend API 기본 URL |
| `VITE_LOCAL_URI` | 로컬 Backend URI |
| `VITE_SENTRY_DSN` | Sentry DSN |
