# 개발 환경 설정 가이드

## 1. 환경 변수 설정

```bash
# backend-secret.env 파일 생성
-> secret 
```

## 2. 데이터베이스 실행 (Docker)

```bash
# MySQL + Redis 컨테이너 실행
docker compose -f docker-compose.local.yml up -d

# 컨테이너 상태 확인
docker compose -f docker-compose.local.yml ps

# 로그 확인
docker compose -f docker-compose.local.yml logs -f
```

## 3. Spring Boot 로컬 실행

```bash
# Gradle로 Spring Boot 실행 (자동으로 dev 프로파일 사용)
./gradlew bootRun

# 또는 IDE에서 BackendApplication.main() 실행
# application-dev.yml이 자동으로 로드됩니다
```

## 4. 연결 확인

- **Spring Boot**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **MySQL**: localhost:3306
- **Redis**: localhost:6379

## 5. 전체 Docker 환경 실행 (선택사항)

```bash
# Spring Boot + MySQL + Redis 모두 Docker로 실행
docker compose up -d

# 로그 확인
docker compose logs -f backend
```

## 6. 데이터베이스 정리

```bash
# 로컬 개발용 컨테이너 중지 및 제거
docker compose -f docker-compose.local.yml down

# 볼륨까지 제거 (데이터 완전 삭제)
docker compose -f docker-compose.local.yml down -v

# 전체 환경 정리
docker compose down -v
```

## 7. 문제 해결

### MySQL 연결 실패

- 컨테이너가 완전히 시작될 때까지 대기 (health check 통과 후)
- 포트 3306이 이미 사용 중인지 확인
- `backend-secret.env` 파일이 올바르게 설정되었는지 확인

### Spring Boot 실행 실패

- Java 21이 설치되어 있는지 확인
- Gradle 버전 확인 (8.7 이상)
- 의존성 다운로드: `./gradlew build`
- `backend-secret.env` 파일이 존재하는지 확인

### 환경 변수 문제

- `application-dev.yml`에서 기본값이 제공되므로 필수 환경 변수만 설정
- JWT_SECRET은 Base64 인코딩된 값이어야 함
- AWS 설정은 로컬에서 비활성화됨 (필요시 주석 해제)
