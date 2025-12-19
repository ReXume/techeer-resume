# Techeer Resume Backend

Spring Boot 기반 이력서 관리 백엔드 서비스

## 🚀 실행 방법

### 1. 로컬 실행 (IDE)

**사용 프로파일**: `local`  
**설정 파일**: `application-local.yml`  
**데이터베이스**: localhost:3306 (Docker Compose로 별도 실행 필요)

```bash
# 1. 데이터베이스만 Docker로 실행
cd backend
docker compose -f docker-compose.local.yml up -d

# 2. IDE에서 직접 실행 또는
./gradlew bootRun

# 3. 또는 JAR 빌드 후 실행
./gradlew bootJar
java -jar build/libs/*.jar --spring.profiles.active=local
```

**특징**:
- 가장 빠른 개발 속도
- IDE 디버거 사용 가능
- Hot Reload (IDE 자동 재시작)
- MySQL, Redis만 Docker로 실행

---

### 2. Docker Dev 실행 (개발 환경)

**사용 프로파일**: `dev`  
**설정 파일**: `application-dev.yml`  
**Dockerfile**: `Dockerfile.dev`  
**데이터베이스**: Docker 네트워크 내부 (mysql:3306)

```bash
cd backend

# 1. 개발 환경 Docker Compose 실행
docker compose -f docker-compose.dev.yml up --build

# 2. 백그라운드 실행
docker compose -f docker-compose.dev.yml up -d --build

# 3. 로그 확인
docker compose -f docker-compose.dev.yml logs -f backend

# 4. 중지
docker compose -f docker-compose.dev.yml down
```

**특징**:
- **Spring Boot DevTools 활성화** (자동 재시작)
- **바인딩 마운트** (`./src` → `/app/src`)
- 코드 변경 시 자동 반영 (약 5-10초 소요)
- LiveReload 지원 (포트 35729)
- Gradle 캐시 볼륨으로 빌드 속도 향상
- 전체 환경이 Docker로 격리됨

**코드 변경 시 동작**:
1. `src/` 디렉토리 파일 수정
2. DevTools가 변경 감지
3. 자동으로 애플리케이션 재시작
4. 브라우저 LiveReload (확장 프로그램 필요)

---

### 3. Docker Prod 실행 (프로덕션)

**사용 프로파일**: `docker`  
**설정 파일**: `application-docker.yml`  
**Dockerfile**: `Dockerfile`  
**데이터베이스**: Docker 네트워크 내부 (mysql:3306)

```bash
cd backend

# 1. 프로덕션 환경 Docker Compose 실행
docker compose up --build

# 2. 백그라운드 실행
docker compose up -d --build

# 3. 로그 확인
docker compose logs -f backend

# 4. 중지
docker compose down
```

**특징**:
- Multi-stage 빌드 (최적화된 이미지)
- JAR 파일만 포함 (소스 코드 미포함)
- 테스트 제외 빌드 (`-x test`)
- 프로덕션 최적화 설정
- 코드 변경 시 재빌드 필요

---

## 📊 실행 방법 비교

| 항목 | 로컬 실행 (IDE) | Docker Dev | Docker Prod |
|------|----------------|------------|-------------|
| **프로파일** | `local` | `dev` | `docker` |
| **설정 파일** | `application-local.yml` | `application-dev.yml` | `application-docker.yml` |
| **Dockerfile** | - | `Dockerfile.dev` | `Dockerfile` |
| **Docker Compose** | `docker-compose.local.yml` | `docker-compose.dev.yml` | `docker-compose.yml` |
| **DB 호스트** | `localhost` | `mysql` (컨테이너) | `mysql` (컨테이너) |
| **Hot Reload** | IDE 자동 재시작 | DevTools (5-10초) | ❌ (재빌드 필요) |
| **디버거** | ✅ | ⚠️ (원격 디버깅) | ❌ |
| **시작 속도** | ⚡ 빠름 | 🐢 느림 (Gradle) | 🚀 중간 (JAR) |
| **격리성** | ❌ | ✅ | ✅ |
| **용도** | 일반 개발 | Docker 환경 테스트 | 배포/프로덕션 |

---

## 🔧 환경 변수 설정

`backend-secret.env` 파일을 생성하고 다음 내용을 설정하세요:

```env
# MySQL
MYSQL_ROOT_PASSWORD=root1234
MYSQL_DATABASE=techeer
MYSQL_USER=sa
MYSQL_PASSWORD=1234

# Redis
REDIS_PASSWORD=

# JWT
JWT_SECRET=your-base64-encoded-secret-key

# OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GOOGLE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google

GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
GITHUB_REDIRECT_URI=http://localhost:8080/login/oauth2/code/github

# GCP
GCP_PROJECT_ID=your-project-id
GCP_SERVICE_ACCOUNT_KEY_PATH=/path/to/service-account-key.json
GCS_BUCKET_NAME=your-bucket-name
GCS_REGION=asia-northeast3
GCS_PROFILE_FOLDER=profile
GCS_DOCUMENT_FOLDER=document
GCS_VERIFICATION_FOLDER=verification
```

---

## 📝 개발 워크플로우 권장사항

### 일반 개발 작업
```bash
# 1. 데이터베이스 시작
docker compose -f docker-compose.local.yml up -d

# 2. IDE에서 애플리케이션 실행 (프로파일: local)
# IntelliJ: Run Configuration에서 Active profiles: local 설정
```

### Docker 환경 테스트
```bash
# Docker에서 전체 스택 실행 (코드 변경 자동 반영)
docker compose -f docker-compose.dev.yml up --build
```

### 프로덕션 빌드 테스트
```bash
# 프로덕션 이미지 빌드 및 실행
docker compose up --build
```

---

## 🐛 트러블슈팅

### Docker Dev 환경에서 코드 변경이 반영되지 않을 때

```bash
# 1. 컨테이너 재시작
docker compose -f docker-compose.dev.yml restart backend

# 2. 볼륨 캐시 문제 시 재빌드
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.dev.yml up --build --force-recreate
```

### 포트 충돌 시

```bash
# 실행 중인 컨테이너 확인
docker ps

# 특정 포트 사용 프로세스 확인 (macOS)
lsof -i :8080
lsof -i :3306
lsof -i :6379

# 기존 컨테이너 중지
docker compose down
docker compose -f docker-compose.local.yml down
docker compose -f docker-compose.dev.yml down
```

---

## 📚 참고 문서

- [API 기능 목록](./docs/API_FEATURES.md)
- [테스트 가이드](./docs/TEST_GUIDE.md)
- [Spring Java Format 가이드](./docs/SPRING_JAVA_FORMAT_GUIDE.md)
- [Swagger UI](http://localhost:8080/swagger-ui/index.html)

