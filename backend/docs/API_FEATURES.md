# API 기능 목록

현재 구현된 API 기능들을 도메인별로 정리한 문서입니다.

## 📋 목차

- [User (회원)](#user-회원)
- [Company (기업)](#company-기업)
- [CompanyLike (기업 좋아요)](#companylike-기업-좋아요)
- [JobPosting (채용공고)](#jobposting-채용공고)
- [Application (지원)](#application-지원)
- [Bookmark (북마크)](#bookmark-북마크)
- [Resume (이력서)](#resume-이력서)
- [Portfolio (포트폴리오)](#portfolio-포트폴리오)
- [Education (학력)](#education-학력)
- [UserCareer (경력)](#usercareer-경력)
- [UserSkill (스킬)](#userskill-스킬)

---

## User (회원)

<details>
<summary><b>회원가입 (자체 로그인)</b></summary>

- **Endpoint**: `POST /api/v1/auth/register`
- **Request**: `RegisterRequest` (email, password, name, role)
- **Response**: `ApiResponse<Void>`
- **권한**: 인증 불필요
</details>

<details>
<summary><b>로그인 (자체 로그인)</b></summary>

- **Endpoint**: `POST /api/v1/auth/login`
- **Request**: `LoginRequest` (email, password, socialType)
- **Response**: `ApiResponse<Void>` + JWT 쿠키 설정
- **권한**: 인증 불필요
</details>

<details>
<summary><b>소셜 로그인 (OAuth2)</b></summary>

- **Endpoint**: `GET /oauth2/authorization/{provider}` (google, kakao, naver 등)
- **Response**: 리다이렉트 후 JWT 쿠키 설정
- **권한**: 인증 불필요
</details>

<details>
<summary><b>추가 정보 입력</b></summary>

- **Endpoint**: `POST /api/v1/user`
- **Request**: `SignUpRequest` (name, role)
- **Response**: `ApiResponse<Void>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>유저 정보 조회</b></summary>

- **Endpoint**: `GET /api/v1/user`
- **Response**: `ApiResponse<UserInfoResponse>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>프로필 이미지 수정</b></summary>

- **Endpoint**: `PATCH /api/v1/user/profile-image`
- **Request**: `MultipartFile` (file)
- **Response**: `ApiResponse<String>` (이미지 URL)
- **권한**: 인증 필요
</details>

<details>
<summary><b>로그아웃</b></summary>

- **Endpoint**: `POST /api/v1/logout`
- **Response**: `ApiResponse<Void>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>액세스 토큰 재발급</b></summary>

- **Endpoint**: `POST /api/v1/reissue`
- **Request**: 쿠키에서 refreshToken
- **Response**: `ApiResponse<Void>` + 새로운 accessToken 쿠키
- **권한**: 인증 불필요 (refreshToken 필요)
</details>

---

## Company (기업)

<details>
<summary><b>기업 등록</b></summary>

- **UseCase**: `RegisterCompanyUseCase`
- **Endpoint**: `POST /api/v1/companies`
- **Request**: `CompanyRegisterRequest` (name, industryDomain, websiteUrl, location)
- **Response**: `ApiResponse<Long>` (companyId)
- **권한**: 인증 필요
- **비즈니스 로직**: 등록한 사용자는 자동으로 ADMIN 권한 부여
</details>

<details>
<summary><b>기업 단건 조회</b></summary>

- **UseCase**: `GetCompanyUseCase`
- **Endpoint**: `GET /api/v1/companies/{companyId}`
- **Response**: `ApiResponse<CompanyInfoResponse>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>기업 정보 수정</b></summary>

- **UseCase**: `UpdateCompanyUseCase`
- **Endpoint**: `PUT /api/v1/companies/{companyId}`
- **Request**: `CompanyUpdateRequest` (name, industryDomain, websiteUrl, location)
- **Response**: `ApiResponse<Void>`
- **권한**: 기업 ADMIN만 가능
</details>

<details>
<summary><b>기업 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteCompanyUseCase`
- **Endpoint**: `DELETE /api/v1/companies/{companyId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 기업 ADMIN만 가능
</details>

---

## CompanyLike (기업 좋아요)

<details>
<summary><b>기업 좋아요</b></summary>

- **UseCase**: `LikeCompanyUseCase`
- **Endpoint**: `POST /api/v1/company-likes`
- **Request**: `CompanyLikeCreateRequest` (companyId)
- **Response**: `ApiResponse<Long>` (likeId)
- **권한**: 인증 필요
- **비즈니스 로직**: 중복 좋아요 불가
</details>

<details>
<summary><b>좋아요 단건 조회</b></summary>

- **UseCase**: `GetCompanyLikeUseCase`
- **Endpoint**: `GET /api/v1/company-likes/{companyLikeId}`
- **Response**: `ApiResponse<CompanyLikeInfoResponse>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>좋아요 취소 (Soft Delete)</b></summary>

- **UseCase**: `UnlikeCompanyUseCase`
- **Endpoint**: `DELETE /api/v1/company-likes/{companyLikeId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능
</details>

---

## JobPosting (채용공고)

<details>
<summary><b>채용공고 등록</b></summary>

- **UseCase**: `CreateJobPostingUseCase`
- **Endpoint**: `POST /api/v1/job-postings`
- **Request**: `JobPostingCreateRequest` (companyId, title, contents, expYears)
- **Response**: `ApiResponse<Long>` (jobPostingId)
- **권한**: 해당 기업의 ADMIN만 가능
</details>

<details>
<summary><b>채용공고 단건 조회</b></summary>

- **UseCase**: `GetJobPostingUseCase`
- **Endpoint**: `GET /api/v1/job-postings/{jobPostingId}`
- **Response**: `ApiResponse<JobPostingInfoResponse>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>채용공고 수정</b></summary>

- **UseCase**: `UpdateJobPostingUseCase`
- **Endpoint**: `PUT /api/v1/job-postings/{jobPostingId}`
- **Request**: `JobPostingUpdateRequest` (title, contents, expYears)
- **Response**: `ApiResponse<Void>`
- **권한**: 해당 기업의 ADMIN만 가능
</details>

<details>
<summary><b>채용공고 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteJobPostingUseCase`
- **Endpoint**: `DELETE /api/v1/job-postings/{jobPostingId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 해당 기업의 ADMIN만 가능
</details>

---

## Application (지원)

<details>
<summary><b>채용공고 지원</b></summary>

- **UseCase**: `ApplyJobUseCase`
- **Endpoint**: `POST /api/v1/applications`
- **Request**: `ApplicationApplyRequest` (jobPostingId)
- **Response**: `ApiResponse<Long>` (applicationId)
- **권한**: 인증 필요
- **비즈니스 로직**: 중복 지원 불가
</details>

<details>
<summary><b>지원 내역 단건 조회</b></summary>

- **UseCase**: `GetApplicationUseCase`
- **Endpoint**: `GET /api/v1/applications/{applicationId}`
- **Response**: `ApiResponse<ApplicationInfoResponse>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>지원 취소 (Soft Delete)</b></summary>

- **UseCase**: `CancelApplicationUseCase`
- **Endpoint**: `DELETE /api/v1/applications/{applicationId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능
</details>

---

## Bookmark (북마크)

<details>
<summary><b>채용공고 북마크</b></summary>

- **UseCase**: `BookmarkJobPostingUseCase`
- **Endpoint**: `POST /api/v1/bookmarks`
- **Request**: `BookmarkCreateRequest` (jobPostingId)
- **Response**: `ApiResponse<Long>` (bookmarkId)
- **권한**: 인증 필요
- **비즈니스 로직**: 중복 북마크 불가
</details>

<details>
<summary><b>북마크 단건 조회</b></summary>

- **UseCase**: `GetBookmarkUseCase`
- **Endpoint**: `GET /api/v1/bookmarks/{bookmarkId}`
- **Response**: `ApiResponse<BookmarkInfoResponse>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>북마크 취소 (Soft Delete)</b></summary>

- **UseCase**: `CancelBookmarkUseCase`
- **Endpoint**: `DELETE /api/v1/bookmarks/{bookmarkId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능
</details>

---

## Resume (이력서)

<details>
<summary><b>이력서 등록</b></summary>

- **UseCase**: `CreateResumeUseCase`
- **Endpoint**: `POST /api/v1/resumes`
- **Request**:
  - `ResumeCreateRequest` (title, isDefault) - JSON
  - `MultipartFile` (file) - PDF, Word, Excel
- **Response**: `ApiResponse<Long>` (resumeId)
- **권한**: 인증 필요
- **파일 업로드**: GCS에 저장 후 UserFile 엔티티 생성
</details>

<details>
<summary><b>이력서 단건 조회</b></summary>

- **UseCase**: `GetResumeUseCase`
- **Endpoint**: `GET /api/v1/resumes/{resumeId}`
- **Response**: `ApiResponse<ResumeInfoResponse>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>이력서 수정</b></summary>

- **UseCase**: `UpdateResumeUseCase`
- **Endpoint**: `PUT /api/v1/resumes/{resumeId}`
- **Request**: `ResumeUpdateRequest` (title, isDefault)
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능
</details>

<details>
<summary><b>이력서 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteResumeUseCase`
- **Endpoint**: `DELETE /api/v1/resumes/{resumeId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능
</details>

---

## Portfolio (포트폴리오)

<details>
<summary><b>포트폴리오 등록</b></summary>

- **UseCase**: `CreatePortfolioUseCase`
- **Endpoint**: `POST /api/v1/portfolios`
- **Request**:
  - `PortfolioCreateRequest` (title, isDefault) - JSON
  - `MultipartFile` (file) - PDF, 이미지 등
- **Response**: `ApiResponse<Long>` (portfolioId)
- **권한**: 인증 필요
- **파일 업로드**: GCS에 저장 후 UserFile 엔티티 생성
</details>

---

## Education (학력)

<details>
<summary><b>학력 등록</b></summary>

- **UseCase**: `CreateEducationUseCase`
- **Endpoint**: `POST /api/v1/educations`
- **Request**:
  - `EducationCreateRequest` (title, isDefault) - JSON
  - `MultipartFile` (file) - 증명 파일 (PDF, 이미지 등)
- **Response**: `ApiResponse<Long>` (educationId)
- **권한**: 인증 필요
- **파일 업로드**: GCS verification 폴더에 저장
</details>

---

## UserCareer (경력)

<details>
<summary><b>경력 등록</b></summary>

- **UseCase**: `CreateUserCareerUseCase`
- **Endpoint**: `POST /api/v1/user-careers`
- **Request**: `UserCareerCreateRequest` (companyName, jobTitle, isCurrent, startDate, endDate)
- **Response**: `ApiResponse<Long>` (careerId)
- **권한**: 인증 필요
</details>

<details>
<summary><b>경력 단건 조회</b></summary>

- **UseCase**: `GetUserCareerUseCase`
- **Endpoint**: `GET /api/v1/user-careers/{careerId}`
- **Response**: `ApiResponse<UserCareerInfoResponse>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>경력 수정</b></summary>

- **UseCase**: `UpdateUserCareerUseCase`
- **Endpoint**: `PUT /api/v1/user-careers/{careerId}`
- **Request**: `UserCareerUpdateRequest` (companyName, jobTitle, isCurrent, startDate, endDate)
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능
</details>

<details>
<summary><b>경력 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteUserCareerUseCase`
- **Endpoint**: `DELETE /api/v1/user-careers/{careerId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능
</details>

---

## UserSkill (스킬)

<details>
<summary><b>스킬 등록</b></summary>

- **UseCase**: `CreateUserSkillUseCase`
- **Endpoint**: `POST /api/v1/user-skills`
- **Request**: `UserSkillCreateRequest` (skillId)
- **Response**: `ApiResponse<Long>` (userSkillId)
- **권한**: 인증 필요
- **비즈니스 로직**: 중복 스킬 등록 불가
</details>

<details>
<summary><b>스킬 단건 조회</b></summary>

- **UseCase**: `GetUserSkillUseCase`
- **Endpoint**: `GET /api/v1/user-skills/{userSkillId}`
- **Response**: `ApiResponse<UserSkillInfoResponse>`
- **권한**: 인증 필요
</details>

<details>
<summary><b>스킬 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteUserSkillUseCase`
- **Endpoint**: `DELETE /api/v1/user-skills/{userSkillId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능
</details>

---

## 🔐 인증 방식

### JWT 토큰

- **Access Token**: 1시간 (쿠키 또는 Authorization 헤더)
- **Refresh Token**: 7일 (쿠키)

### Swagger UI 사용 방법

1. `/api/v1/mock/signup?id=testuser`로 테스트 유저 생성 및 토큰 발급
2. 응답에서 받은 `accessToken` 복사
3. Swagger UI 우측 상단 "Authorize" 버튼 클릭
4. 토큰 입력 (Bearer 접두사는 자동 추가)

---

## 📊 통계

- **총 도메인**: 11개
- **총 API 엔드포인트**: 30개
- **총 UseCase**: 30개
- **인증 필요 API**: 27개
- **공개 API**: 3개 (회원가입, 로그인, OAuth2)

---

## 🏗️ 아키텍처

### Hexagonal Architecture (Port & Adapter)

```
domain/              # 도메인 엔티티
application/
  ├─ port/
  │   ├─ in/        # UseCase 인터페이스
  │   └─ out/       # Repository 인터페이스
  └─ service/       # UseCase 구현체
adapter/
  ├─ in/
  │   └─ web/       # Controller
  └─ out/
      └─ persistence/ # JPA Repository
```

### 인증 처리 흐름

```
Controller → UserService.getLoginUser() → userId 추출
          → UseCase.execute(request, userId)
          → Service에서 LoadUserPort.findById(userId)
          → 비즈니스 로직 수행
```

---

## 📝 참고 문서

- [테스트 가이드](./TEST_GUIDE.md)
- [Spring Java Format 가이드](./SPRING_JAVA_FORMAT_GUIDE.md)
- [Spring REST Docs](../src/docs/asciidoc/index.adoc)
