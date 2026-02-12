# Techeer Resume: 백엔드 시스템 요구사항 명세서 (PRD)

## 1. 시스템 개요 (System Overview)

### 1.1. 목적
본 문서는 'Techeer Resume' 플랫폼의 백엔드 시스템이 제공해야 하는 기능, 제약 조건, 아키텍처 및 기술 명세를 정의하는 것을 목적으로 한다. 백엔드 시스템은 플랫폼의 모든 비즈니스 로직, 데이터 처리 및 저장을 담당하며, 프론트엔드 애플리케이션에 안정적이고 효율적인 API를 제공한다.

### 1.2. 주요 아키텍처 (Hexagonal Architecture)
백엔드 시스템은 **Hexagonal Architecture (Ports & Adapters)** 구조를 채택하여 비즈니스 로직(Domain)을 외부 기술(Adapter)로부터 분리한다. 이를 통해 시스템의 유연성, 확장성, 테스트 용이성을 극대화한다.

- **Domain**: 시스템의 핵심 비즈니스 로직과 데이터 모델(Entity)을 포함하며, 다른 계층에 의존하지 않는 순수한 영역이다.
- **Application (UseCases)**: `application/port/in`에 정의된 UseCase 인터페이스를 `application/service`에서 구현하며, 도메인 로직을 조합하여 실제 사용 사례를 처리한다. 외부와의 통신은 Port(in/out)를 통해 이루어진다.
- **Adapter**: 외부 세계와의 상호작용을 담당한다.
    - **Inbound Adapter (`adapter/in/web`)**: 외부 요청(HTTP)을 받아 UseCase를 호출하는 컨트롤러(Controller)를 포함한다.
    - **Outbound Adapter (`adapter/out/persistence`, `adapter/out/gcs`)**: UseCase의 요청에 따라 데이터베이스(JPA), 외부 스토리지(GCS) 등과 상호작용한다.

### 1.3. 핵심 기술 스택 (Core Technology Stack)
- **언어/프레임워크**: Java 17, Spring Boot 3.x
- **데이터베이스**: MySQL (Primary), Redis (캐싱, Refresh Token 등)
- **인증**: Spring Security, JWT (JSON Web Token)
- **파일 저장소**: Google Cloud Storage (GCS)
- **빌드/의존성 관리**: Gradle
- **컨테이너화**: Docker, Docker Compose
- **API 문서화**: Spring REST Docs, Swagger UI

## 2. 사용자 및 권한 (Users & Authorization)

### 2.1. 사용자 역할 (Roles)
- **`ROLE_USER`**: 일반 가입 사용자. 자신의 정보 및 커리어 자산을 관리하고 채용 공고에 지원할 수 있다.
- **`ROLE_ADMIN`**: 특정 기업의 관리자. `ROLE_USER`의 모든 권한과 더불어, 소속된 기업의 정보와 채용 공고를 관리할 수 있다.

### 2.2. 인증 (Authentication)
- **자체 로그인**: 이메일/비밀번호 기반의 로그인. 성공 시 JWT(Access/Refresh Token)가 발급된다.
- **소셜 로그인**: OAuth2 프로토콜을 통해 Google, GitHub 계정으로 로그인한다.
- **토큰 관리**:
    - **Access Token (1시간)**: API 요청 시 `Authorization` 헤더(Bearer) 또는 쿠키를 통해 전송된다.
    - **Refresh Token (7일)**: Redis에 사용자 ID와 매핑되어 저장되며, Access Token 재발급에 사용된다.

### 2.3. 인가 (Authorization)
- API 접근은 Spring Security와 어노테이션(`@PreAuthorize`)을 통해 역할 기반으로 통제된다.
- 리소스 수정/삭제 요청 시, 해당 리소스의 소유자이거나 적절한 `ROLE_ADMIN` 권한을 가졌는지 검증한다.

## 3. 도메인별 기능 명세 (Detailed Feature Specification)

### 3.1. 사용자 (User)
- **설명**: 플랫폼의 기본이 되는 사용자 계정 관리 기능.
- **기능**:
    - 자체 회원가입, 소셜 회원가입 (OAuth2)
    - 로그인, 로그아웃, 토큰 재발급
    - 사용자 정보 조회 및 추가 정보 입력
    - 프로필 이미지 수정
- **주요 API Endpoints**:
    - `POST /api/v1/auth/register`: 자체 회원가입
    - `POST /api/v1/auth/login`: 로그인
    - `GET /oauth2/authorization/{provider}`: 소셜 로그인 시작
    - `GET /api/v1/user`: 자신의 정보 조회
    - `PATCH /api/v1/user/profile-image`: 프로필 이미지 업로드
- **비즈니스 로직**:
    - 회원가입 시 이메일 중복을 검사하고 비밀번호는 BCrypt로 암호화하여 저장한다.
    - 프로필 이미지 업로드 시 파일을 GCS `profile` 폴더에 저장하고, 해당 URL을 `User` 엔티티에 업데이트한다.
- **데이터 모델**: `User`

### 3.2. 이력서 및 문서 (Resume, Portfolio, Education)
- **설명**: 사용자의 핵심 커리어 자산인 문서 파일을 관리한다.
- **기능**:
    - 이력서, 포트폴리오, 학력 증명서 등 문서 파일 업로드 및 정보(제목, 기본값 여부) 등록
    - 문서 목록 및 단건 상세 조회
    - 문서 정보 수정 및 파일 삭제 (Soft Delete)
- **주요 API Endpoints**:
    - `POST /api/v1/resumes`: 이력서 등록 (파일 + JSON)
    - `GET /api/v1/resumes`: 이력서 목록 조회 (페이지네이션)
    - `GET /api/v1/resumes/{resumeId}`: 이력서 단건 조회
    - `DELETE /api/v1/resumes/{resumeId}`: 이력서 삭제
    - (포트폴리오, 학력도 유사한 패턴의 엔드포인트 제공)
- **비즈니스 로직**:
    - `multipart/form-data` 요청을 통해 JSON 데이터(`ResumeCreateRequest`)와 파일(`MultipartFile`)을 동시에 처리한다.
    - 업로드된 파일은 GCS 내 지정된 폴더(`document`, `verification` 등)에 저장된다.
    - 파일의 메타데이터(원본 파일명, 저장 경로, URL 등)는 `UserFile` 엔티티에, 문서 정보는 각 `Resume`, `Portfolio` 엔티티에 저장된다.
    - 삭제는 `Soft Delete`로 구현하여 `deletedAt` 필드를 업데이트한다.
- **데이터 모델**: `Resume`, `Portfolio`, `Education`, `UserFile` (Polymorphic 관계)

### 3.3. 기업 및 채용 (Company & Job Posting)
- **설명**: 기업 정보와 해당 기업이 게시하는 채용 공고를 관리한다.
- **기능**:
    - 기업 정보 등록, 조회, 수정, 삭제
    - 채용 공고 등록, 조회, 수정, 삭제
- **주요 API Endpoints**:
    - `POST /api/v1/companies`: 기업 등록
    - `GET /api/v1/companies/{companyId}`: 기업 상세 조회
    - `PUT /api/v1/companies/{companyId}`: 기업 정보 수정
    - `POST /api/v1/job-postings`: 채용 공고 등록
- **비즈니스 로직**:
    - 기업을 등록한 사용자는 해당 `Company`의 `ADMIN` 역할을 자동으로 부여받는다.
    - 채용 공고의 생성/수정/삭제는 해당 기업의 `ADMIN` 역할을 가진 사용자만 가능하다.
- **데이터 모델**: `Company`, `JobPosting`, `UserCompany` (사용자와 기업의 관계 및 역할 매핑)

### 3.4. 지원 및 관심 표현 (Application & Engagement)
- **설명**: 사용자와 채용 공고/기업 간의 상호작용을 관리한다.
- **기능**:
    - 채용 공고에 지원 및 지원 취소
    - 관심 있는 채용 공고 북마크 및 취소
    - 관심 있는 기업 '좋아요' 및 취소
- **주요 API Endpoints**:
    - `POST /api/v1/applications`: 채용 공고 지원
    - `DELETE /api/v1/applications/{applicationId}`: 지원 취소
    - `POST /api/v1/bookmarks`: 북마크 등록
    - `POST /api/v1/company-likes`: 기업 좋아요
- **비즈니스 로직**:
    - 동일한 공고에 대한 중복 지원, 중복 북마크, 중복 '좋아요'를 방지하는 로직이 포함된다.
    - 모든 취소 기능은 `Soft Delete`로 구현된다.
- **데이터 모델**: `Application`, `Bookmark`, `CompanyLike`

### 3.5. 경력 및 스킬 (Career & Skill)
- **설명**: 사용자의 경력 사항과 보유 기술 스택을 관리한다.
- **기능**:
    - 과거 및 현재 경력 정보 등록, 수정, 삭제
    - 시스템에 등록된 기술 스택 조회
    - 사용자가 보유한 기술 스택 등록 및 삭제
- **주요 API Endpoints**:
    - `POST /api/v1/user-careers`: 경력 등록
    - `GET /api/v1/skills`: 전체 기술 스택 조회
    - `POST /api/v1/user-skills`: 사용자의 보유 스킬 등록
- **비즈니스 로직**:
    - 경력 등록 시 재직 여부(`isCurrent`)에 따라 종료일(`endDate`) 필드의 유효성을 검증한다.
    - 기술 스택(`Skill`)은 시스템 관리자가 사전에 등록하며, 이름 중복(대소문자 무시)을 허용하지 않는다.
    - `UserSkill`은 `User`와 `Skill`의 다대다(N:M) 관계를 매핑하는 조인 테이블 역할을 한다.
- **데이터 모델**: `UserCareer`, `Skill`, `UserSkill`

## 4. 데이터베이스 모델 (Data Model)
- `User`를 중심으로 대부분의 엔티티가 1:N 관계를 맺는다. (e.g., `User` 1 - N `Resume`)
- N:M 관계는 조인 테이블을 통해 매핑된다. (e.g., `User` N - M `Skill` via `UserSkill`)
- `UserFile`은 polymorphic 설계를 통해 `Resume`, `Portfolio` 등 다양한 문서 타입을 참조할 수 있다.
- 모든 엔티티는 `BaseTimeEntity`를 상속하여 생성/수정 시간을 자동으로 기록하며, 대부분 `Soft Delete`를 위해 `deletedAt` 필드를 포함한다.

## 5. 인프라 및 배포 (Infrastructure & Deployment)
- **실행 환경**: `local`, `dev`, `docker` 세 가지 프로파일을 통해 환경별(로컬 개발, 컨테이너 개발, 프로덕션) 설정을 분리한다. (`application-{profile}.yml`)
- **컨테이너화**:
    - `docker-compose.yml`: 프로덕션 환경용 Docker Compose 파일. Multi-stage 빌드를 적용한 `Dockerfile`을 사용한다.
    - `docker-compose.dev.yml`: 개발 환경용 Docker Compose 파일. Spring Boot DevTools와 바인드 마운트를 활용하여 코드 변경 시 자동 재시작(Hot Reload)을 지원한다.
- **CI/CD**:
    - GitHub Actions 워크플로우(`.github/workflows`)를 통해 Pull Request 생성 시 자동으로 Linter, Test, Build, Security Scan 등이 실행되어 코드 품질과 안정성을 유지한다.
