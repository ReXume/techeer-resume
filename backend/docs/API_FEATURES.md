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
- **Request**: `RegisterRequest` (email, password, username)
- **Response**: `ApiResponse<Void>`
- **권한**: 인증 불필요

**curl 명령어:**

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "username": "테스트유저",
    "password": "Test1234!"
  }'
```

</details>

<details>
<summary><b>로그인 (자체 로그인)</b></summary>

- **Endpoint**: `POST /api/v1/auth/login`
- **Request**: `LoginRequest` (email, password)
- **Response**: `ApiResponse<Void>` + JWT 쿠키 설정
- **권한**: 인증 불필요

**curl 명령어:**

```bash
# 로그인 (쿠키에 토큰 저장)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "Test1234!"
  }' \
  -c cookies.txt

# 이후 요청에서 쿠키 사용
curl -X GET http://localhost:8080/api/v1/user \
  -b cookies.txt
```

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
- **Request**: `SignUpRequest` (name)
- **Response**: `ApiResponse<Void>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X POST http://localhost:8080/api/v1/user \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "홍길동"
  }'
```

</details>

<details>
<summary><b>유저 정보 조회</b></summary>

- **Endpoint**: `GET /api/v1/user`
- **Response**: `ApiResponse<UserInfoResponse>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
# 쿠키 사용
curl -X GET http://localhost:8080/api/v1/user \
  -b cookies.txt

# 또는 Authorization 헤더 사용
curl -X GET http://localhost:8080/api/v1/user \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>프로필 이미지 수정</b></summary>

- **Endpoint**: `PATCH /api/v1/user/profile-image`
- **Request**: `MultipartFile` (file)
- **Response**: `ApiResponse<String>` (이미지 URL)
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X PATCH http://localhost:8080/api/v1/user/profile-image \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

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
- **Request**: `CompanyRegisterRequest` (name, companyEmail, industryDomain, websiteUrl, location)
- **Response**: `ApiResponse<Long>` (companyId)
- **권한**: 인증 필요
- **비즈니스 로직**: 등록한 사용자는 자동으로 ADMIN 권한 부여

**curl 명령어 (snake_case 필수, 모든 필드 포함):**

```bash
curl -X POST http://localhost:8080/api/v1/companies \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "네이버",
    "company_email": "recruit@naver.com",
    "industry_domain": "IT/인터넷",
    "website_url": "https://www.naver.com",
    "location": "경기도 성남시 분당구"
  }'
```

**필드 설명:**

- `name` (필수): 기업명
- `company_email` (선택): 기업 이메일
- `industry_domain` (선택): 산업 분야
- `website_url` (선택): 웹사이트 URL
- `location` (선택): 위치

</details>

<details>
<summary><b>기업 단건 조회</b></summary>

- **UseCase**: `GetCompanyUseCase`
- **Endpoint**: `GET /api/v1/companies/{companyId}`
- **Response**: `ApiResponse<CompanyInfoResponse>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/companies/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>기업 정보 수정</b></summary>

- **UseCase**: `UpdateCompanyUseCase`
- **Endpoint**: `PUT /api/v1/companies/{companyId}`
- **Request**: `CompanyUpdateRequest` (name, companyEmail, industryDomain, websiteUrl, location)
- **Response**: `ApiResponse<Void>`
- **권한**: 기업 ADMIN만 가능

**curl 명령어:**

```bash
curl -X PUT http://localhost:8080/api/v1/companies/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "테크컴퍼니 (수정)",
    "companyEmail": "newcontact@techcompany.com",
    "industryDomain": "IT/소프트웨어",
    "websiteUrl": "https://techcompany.com",
    "location": "서울시 강남구"
  }'
```

</details>

<details>
<summary><b>기업 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteCompanyUseCase`
- **Endpoint**: `DELETE /api/v1/companies/{companyId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 기업 ADMIN만 가능

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/companies/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

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

**curl 명령어 (snake_case 필수, user_id 필수):**

```bash
curl -X POST http://localhost:8080/api/v1/company-likes \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{
    "company_id": 1,
    "user_id": 1
  }'
```

</details>

<details>
<summary><b>좋아요 단건 조회</b></summary>

- **UseCase**: `GetCompanyLikeUseCase`
- **Endpoint**: `GET /api/v1/company-likes/{companyLikeId}`
- **Response**: `ApiResponse<CompanyLikeInfoResponse>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/company-likes/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>좋아요 취소 (Soft Delete)</b></summary>

- **UseCase**: `UnlikeCompanyUseCase`
- **Endpoint**: `DELETE /api/v1/company-likes/{companyLikeId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/company-likes/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

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

**curl 명령어 (snake_case 필수, 모든 필드 포함):**

```bash
curl -X POST http://localhost:8080/api/v1/job-postings \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{
    "company_id": 3,
    "title": "프론트엔드 개발자 채용",
    "contents": "React, TypeScript 경험자 우대\n- 2년 이상 경력\n- 컴포넌트 설계 경험\n- 상태 관리 라이브러리 사용 가능자\n- 협업 능력 우대",
    "exp_years": 2
  }'
```

**필드 설명:**

- `company_id` (필수): 기업 ID
- `title` (필수): 채용공고 제목
- `contents` (선택): 채용공고 내용
- `exp_years` (선택): 요구 경력 연수

</details>

<details>
<summary><b>채용공고 단건 조회</b></summary>

- **UseCase**: `GetJobPostingUseCase`
- **Endpoint**: `GET /api/v1/job-postings/{jobPostingId}`
- **Response**: `ApiResponse<JobPostingInfoResponse>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/job-postings/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>채용공고 수정</b></summary>

- **UseCase**: `UpdateJobPostingUseCase`
- **Endpoint**: `PUT /api/v1/job-postings/{jobPostingId}`
- **Request**: `JobPostingUpdateRequest` (title, contents, expYears)
- **Response**: `ApiResponse<Void>`
- **권한**: 해당 기업의 ADMIN만 가능

**curl 명령어:**

```bash
curl -X PUT http://localhost:8080/api/v1/job-postings/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "title": "백엔드 개발자 채용 (수정)",
    "contents": "Spring Boot 경험자 필수",
    "expYears": 5
  }'
```

</details>

<details>
<summary><b>채용공고 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteJobPostingUseCase`
- **Endpoint**: `DELETE /api/v1/job-postings/{jobPostingId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 해당 기업의 ADMIN만 가능

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/job-postings/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

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

**curl 명령어 (snake_case 필수):**

```bash
curl -X POST http://localhost:8080/api/v1/applications \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{
    "job_posting_id": 1
  }'
```

</details>

<details>
<summary><b>지원 내역 단건 조회</b></summary>

- **UseCase**: `GetApplicationUseCase`
- **Endpoint**: `GET /api/v1/applications/{applicationId}`
- **Response**: `ApiResponse<ApplicationInfoResponse>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/applications/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>지원 취소 (Soft Delete)</b></summary>

- **UseCase**: `CancelApplicationUseCase`
- **Endpoint**: `DELETE /api/v1/applications/{applicationId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/applications/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

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

**curl 명령어 (snake_case 필수):**

```bash
curl -X POST http://localhost:8080/api/v1/bookmarks \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{
    "job_posting_id": 1
  }'
```

</details>

<details>
<summary><b>북마크 단건 조회</b></summary>

- **UseCase**: `GetBookmarkUseCase`
- **Endpoint**: `GET /api/v1/bookmarks/{bookmarkId}`
- **Response**: `ApiResponse<BookmarkInfoResponse>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/bookmarks/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>북마크 전체 조회</b></summary>

- **UseCase**: `GetAllBookmarksUseCase`
- **Endpoint**: `GET /api/v1/bookmarks?page=0&size=10`
- **Response**: `ApiResponse<Slice<BookmarkInfoResponse>>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET "http://localhost:8080/api/v1/bookmarks?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>북마크 취소 (Soft Delete)</b></summary>

- **UseCase**: `CancelBookmarkUseCase`
- **Endpoint**: `DELETE /api/v1/bookmarks/{bookmarkId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/bookmarks/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

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

**curl 명령어:**

```bash
curl -X POST http://localhost:8080/api/v1/resumes \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -F 'request={"title":"내 이력서","isDefault":true};type=application/json' \
  -F 'file=@/path/to/resume.pdf'
```

</details>

<details>
<summary><b>이력서 단건 조회</b></summary>

- **UseCase**: `GetResumeUseCase`
- **Endpoint**: `GET /api/v1/resumes/{resumeId}`
- **Response**: `ApiResponse<ResumeInfoResponse>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/resumes/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>이력서 전체 조회</b></summary>

- **UseCase**: `GetAllResumesUseCase`
- **Endpoint**: `GET /api/v1/resumes?page=0&size=10`
- **Response**: `ApiResponse<Slice<ResumeInfoResponse>>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET "http://localhost:8080/api/v1/resumes?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>이력서 수정</b></summary>

- **UseCase**: `UpdateResumeUseCase`
- **Endpoint**: `PUT /api/v1/resumes/{resumeId}`
- **Request**: `ResumeUpdateRequest` (title, isDefault)
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X PUT http://localhost:8080/api/v1/resumes/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "title": "내 이력서 (수정)",
    "isDefault": false
  }'
```

</details>

<details>
<summary><b>이력서 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteResumeUseCase`
- **Endpoint**: `DELETE /api/v1/resumes/{resumeId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/resumes/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

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

**curl 명령어:**

```bash
curl -X POST http://localhost:8080/api/v1/portfolios \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -F 'request={"title":"내 포트폴리오","isDefault":true};type=application/json' \
  -F 'file=@/path/to/portfolio.pdf'
```

</details>

<details>
<summary><b>포트폴리오 단건 조회</b></summary>

- **UseCase**: `GetPortfolioUseCase`
- **Endpoint**: `GET /api/v1/portfolios/{portfolioId}`
- **Response**: `ApiResponse<PortfolioInfoResponse>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/portfolios/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>포트폴리오 전체 조회</b></summary>

- **UseCase**: `GetAllPortfoliosUseCase`
- **Endpoint**: `GET /api/v1/portfolios?page=0&size=10`
- **Response**: `ApiResponse<Slice<PortfolioInfoResponse>>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET "http://localhost:8080/api/v1/portfolios?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>포트폴리오 수정</b></summary>

- **UseCase**: `UpdatePortfolioUseCase`
- **Endpoint**: `PUT /api/v1/portfolios/{portfolioId}`
- **Request**: `PortfolioUpdateRequest` (title, isDefault)
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X PUT http://localhost:8080/api/v1/portfolios/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "title": "내 포트폴리오 (수정)",
    "isDefault": false
  }'
```

</details>

<details>
<summary><b>포트폴리오 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeletePortfolioUseCase`
- **Endpoint**: `DELETE /api/v1/portfolios/{portfolioId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/portfolios/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

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

**curl 명령어:**

```bash
curl -X POST http://localhost:8080/api/v1/educations \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -F 'request={"title":"학사 학위","isDefault":true};type=application/json' \
  -F 'file=@/path/to/degree.pdf'
```

</details>

<details>
<summary><b>학력 단건 조회</b></summary>

- **UseCase**: `GetEducationUseCase`
- **Endpoint**: `GET /api/v1/educations/{educationId}`
- **Response**: `ApiResponse<EducationInfoResponse>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/educations/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>학력 전체 조회</b></summary>

- **UseCase**: `GetAllEducationsUseCase`
- **Endpoint**: `GET /api/v1/educations?page=0&size=10`
- **Response**: `ApiResponse<Slice<EducationInfoResponse>>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET "http://localhost:8080/api/v1/educations?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>학력 수정</b></summary>

- **UseCase**: `UpdateEducationUseCase`
- **Endpoint**: `PUT /api/v1/educations/{educationId}`
- **Request**: `EducationUpdateRequest` (title, isDefault)
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X PUT http://localhost:8080/api/v1/educations/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "title": "학사 학위 (수정)",
    "isDefault": false
  }'
```

</details>

<details>
<summary><b>학력 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteEducationUseCase`
- **Endpoint**: `DELETE /api/v1/educations/{educationId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/educations/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

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

**curl 명령어 (snake_case 필수, 모든 필드 포함):**

```bash
# 현재 재직 중인 경우
curl -X POST http://localhost:8080/api/v1/user-careers \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{
    "company_name": "테크컴퍼니",
    "job_title": "백엔드 개발자",
    "is_current": true,
    "start_date": "2023-01-01",
    "end_date": null
  }'

# 과거 경력인 경우
curl -X POST http://localhost:8080/api/v1/user-careers \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{
    "company_name": "카카오",
    "job_title": "시니어 백엔드 개발자",
    "is_current": false,
    "start_date": "2020-01-01",
    "end_date": "2022-12-31"
  }'
```

**필드 설명:**

- `company_name` (필수): 회사명
- `job_title` (선택): 직책
- `is_current` (선택): 현재 재직 여부
- `start_date` (선택): 시작일 (YYYY-MM-DD)
- `end_date` (선택): 종료일 (YYYY-MM-DD), 현재 재직 중이면 null

</details>

<details>
<summary><b>경력 단건 조회</b></summary>

- **UseCase**: `GetUserCareerUseCase`
- **Endpoint**: `GET /api/v1/user-careers/{careerId}`
- **Response**: `ApiResponse<UserCareerInfoResponse>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/user-careers/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

<details>
<summary><b>경력 수정</b></summary>

- **UseCase**: `UpdateUserCareerUseCase`
- **Endpoint**: `PUT /api/v1/user-careers/{careerId}`
- **Request**: `UserCareerUpdateRequest` (companyName, jobTitle, isCurrent, startDate, endDate)
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X PUT http://localhost:8080/api/v1/user-careers/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "companyName": "테크컴퍼니 (수정)",
    "jobTitle": "시니어 백엔드 개발자",
    "isCurrent": true,
    "startDate": "2023-01-01",
    "endDate": null
  }'
```

</details>

<details>
<summary><b>경력 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteUserCareerUseCase`
- **Endpoint**: `DELETE /api/v1/user-careers/{careerId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/user-careers/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

</details>

---

## Skill (기술 스택)

<details>
<summary><b>기술 스택 등록</b></summary>

- **UseCase**: `CreateSkillUseCase`
- **Endpoint**: `POST /api/v1/skills`
- **Request**: `SkillCreateRequest` (name)
- **Response**: `ApiResponse<Long>` (skillId)
- **권한**: 인증 필요 (모든 사용자 가능)
- **비즈니스 로직**:
  - 스킬명 중복 체크 (대소문자 구분 없이)
  - "Java" 등록 후 "java" 또는 "JAVA" 등록 시도 시 `SKILL_409` 에러 발생

**curl 명령어:**

```bash
curl -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "Java"
  }'
```

**필드 설명:**

- `name` (필수): 기술 스택명 (최대 100자)

**테스트 결과:**

```bash
# 성공 예시
curl -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{"name": "Java"}'
# 응답: {"success":true,"message":"기술 스택이 등록되었습니다.","data":1}

# 중복 등록 시도 (대소문자 구분 없이)
curl -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{"name": "java"}'
# 응답: {"http_status":"CONFLICT","code":"SKILL_409","error_message":"이미 존재하는 스킬입니다."}
```

</details>

<details>
<summary><b>기술 스택 단건 조회</b></summary>

- **UseCase**: `GetSkillUseCase`
- **Endpoint**: `GET /api/v1/skills/{skillId}`
- **Response**: `ApiResponse<SkillInfoResponse>` (id, name)
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/skills/1 \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN"
```

**응답 예시:**

```json
{
  "success": true,
  "message": "기술 스택 조회에 성공했습니다.",
  "data": {
    "id": 1,
    "name": "Java"
  }
}
```

</details>

<details>
<summary><b>기술 스택 수정</b></summary>

- **UseCase**: `UpdateSkillUseCase`
- **Endpoint**: `PUT /api/v1/skills/{skillId}`
- **Request**: `SkillUpdateRequest` (name)
- **Response**: `ApiResponse<Void>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X PUT http://localhost:8080/api/v1/skills/1 \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "Java 17"
  }'
```

**필드 설명:**

- `name` (선택): 기술 스택명 (최대 100자)

**테스트 결과:**

```bash
# 수정 성공
curl -X PUT http://localhost:8080/api/v1/skills/1 \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{"name": "Java 17"}'
# 응답: {"success":true,"message":"기술 스택이 수정되었습니다."}

# 수정 시 중복 체크
curl -X PUT http://localhost:8080/api/v1/skills/1 \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{"name": "Spring Boot"}'
# 응답: {"http_status":"CONFLICT","code":"SKILL_409","error_message":"이미 존재하는 스킬입니다."}
```

</details>

<details>
<summary><b>기술 스택 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteSkillUseCase`
- **Endpoint**: `DELETE /api/v1/skills/{skillId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/skills/1 \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN"
```

**테스트 결과:**

```bash
# 삭제 성공
curl -X DELETE http://localhost:8080/api/v1/skills/2 \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN"
# 응답: {"success":true,"message":"기술 스택이 삭제되었습니다."}

# 삭제 후 조회 시도
curl -X GET http://localhost:8080/api/v1/skills/2 \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN"
# 응답: {"http_status":"NOT_FOUND","code":"SKILL_404","error_message":"스킬을 찾을 수 없습니다."}
```

**참고:**

- Soft Delete로 삭제되므로 `deletedAt`이 설정됩니다. 삭제된 스킬은 조회되지 않습니다.
- 삭제 후 다시 등록 가능 (Soft Delete된 스킬은 중복 체크에서 제외)

**테스트 결과:**

```bash
# 삭제 성공
curl -X DELETE http://localhost:8080/api/v1/skills/6 \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN"
# 응답: {"success":true,"message":"기술 스택이 삭제되었습니다."}

# 삭제 후 조회 시도
curl -X GET http://localhost:8080/api/v1/skills/6 \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN"
# 응답: {"http_status":"NOT_FOUND","code":"SKILL_404","error_message":"스킬을 찾을 수 없습니다."}

# 삭제 후 다시 등록 가능
curl -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{"name": "React"}'
# 응답: {"success":true,"message":"기술 스택이 등록되었습니다.","data":9}
```

</details>

---

## UserSkill (사용자 스킬)

### ⚠️ 사전 준비: Skill 데이터 생성

UserSkill API를 테스트하기 전에 **Skill 데이터를 먼저 생성**해야 합니다. Skill API를 사용하여 기술 스택을 등록하거나, 아래 SQL을 사용하여 직접 생성할 수 있습니다.

**Skill 데이터 생성 SQL:**

```sql
-- 기술 스택 데이터 생성
INSERT INTO skills (name, created_at, updated_at) VALUES
('Java', NOW(), NOW()),
('Spring Boot', NOW(), NOW()),
('Python', NOW(), NOW()),
('JavaScript', NOW(), NOW()),
('TypeScript', NOW(), NOW()),
('React', NOW(), NOW()),
('Node.js', NOW(), NOW()),
('MySQL', NOW(), NOW()),
('PostgreSQL', NOW(), NOW()),
('Redis', NOW(), NOW()),
('Docker', NOW(), NOW()),
('Kubernetes', NOW(), NOW()),
('AWS', NOW(), NOW()),
('Git', NOW(), NOW()),
('JPA', NOW(), NOW()),
('QueryDSL', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();
```

**SQL 실행 방법 (선택사항):**

```bash
# Docker Compose 사용 시
docker compose -f docker-compose.dev.yml exec mysql mysql -uroot -p[비밀번호] techeer < create_skills.sql

# 또는 MySQL 클라이언트로 직접 접속
mysql -h localhost -u root -p techeer < create_skills.sql
```

**또는 Skill API를 사용하여 직접 등록:**

```bash
# Java 등록
curl -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{"name": "Java"}'

# Spring Boot 등록
curl -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{"name": "Spring Boot"}'
```

<details>
<summary><b>스킬 등록</b></summary>

- **UseCase**: `CreateUserSkillUseCase`
- **Endpoint**: `POST /api/v1/user-skills`
- **Request**: `UserSkillCreateRequest` (skillId)
- **Response**: `ApiResponse<Long>` (userSkillId)
- **권한**: 인증 필요
- **비즈니스 로직**: 중복 스킬 등록 불가

**curl 명령어 (snake_case 필수):**

```bash
curl -X POST http://localhost:8080/api/v1/user-skills \
  -H "Content-Type: application/json" \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN" \
  -d '{
    "skill_id": 1
  }'
```

**필드 설명:**

- `skill_id` (필수): Skill ID (DB에 존재하는 skill_id여야 함)

**예시:**

- `skill_id: 1` → Java
- `skill_id: 2` → Spring Boot
- `skill_id: 3` → Python
</details>

</details>

<details>
<summary><b>스킬 단건 조회</b></summary>

- **UseCase**: `GetUserSkillUseCase`
- **Endpoint**: `GET /api/v1/user-skills/{userSkillId}`
- **Response**: `ApiResponse<UserSkillInfoResponse>` (id, userId, skillId, skillName)
- **권한**: 인증 필요

**curl 명령어:**

```bash
curl -X GET http://localhost:8080/api/v1/user-skills/1 \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN"
```

**응답 예시:**

```json
{
  "success": true,
  "message": "성공적으로 처리되었습니다.",
  "data": {
    "id": 1,
    "user_id": 1,
    "skill_id": 1,
    "skill_name": "Java"
  }
}
```

</details>

<details>
<summary><b>스킬 삭제 (Soft Delete)</b></summary>

- **UseCase**: `DeleteUserSkillUseCase`
- **Endpoint**: `DELETE /api/v1/user-skills/{userSkillId}`
- **Response**: `ApiResponse<Void>`
- **권한**: 본인만 가능

**curl 명령어:**

```bash
curl -X DELETE http://localhost:8080/api/v1/user-skills/1 \
  -H "Cookie: accessToken=YOUR_ACCESS_TOKEN"
```

**테스트 시나리오:**

1. 스킬 등록 (skill_id: 1)
2. 스킬 조회 (userSkillId: 1)
3. 중복 스킬 등록 시도 → 에러 발생 (USER_SKILL_ALREADY_EXISTS)
4. 스킬 삭제 (userSkillId: 1)
5. 삭제 후 다시 등록 가능

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

### API 테스트 방법

#### ⚠️ 중요: Request Body는 Snake Case로 전송해야 합니다

서버는 **snake_case**를 자동으로 **camelCase**로 변환합니다. 모든 Request Body는 **snake_case** 형식으로 보내야 합니다.

**예시:**

- ❌ `{"companyId": 1}` → 파싱 실패
- ✅ `{"company_id": 1}` → 성공

#### curl 테스트 방법

**1. Mock 유저 생성 및 토큰 발급:**

```bash
curl -X POST "http://localhost:8080/api/v1/mock/signup?id=testuser"
# 응답에서 accessToken 추출
```

**2. 쿠키 파일 사용 (로그인 후):**

```bash
# 로그인하여 쿠키 저장
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test1234!"}' \
  -c cookies.txt

# 이후 요청에서 쿠키 사용
curl -X GET http://localhost:8080/api/v1/user -b cookies.txt
```

**3. Authorization 헤더 사용:**

```bash
curl -X GET http://localhost:8080/api/v1/user \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### 테스트 결과 요약

**✅ 성공한 API (테스트 완료):**

- 회원가입, 로그인, 유저 정보 조회
- 기업 등록, 조회
- 채용공고 등록, 조회 (snake_case 사용)
- 채용공고 지원 (snake_case 사용)
- 북마크 등록, 조회 (snake_case 사용)
- 기업 좋아요 (snake_case 사용, user_id 필수)
- 경력 등록, 조회 (snake_case 사용)
- 이력서/포트폴리오/학력 전체 조회
- **기술 스택 등록, 조회, 수정, 삭제 (신규 추가)**
  - 대소문자 구분 없이 중복 체크 작동 확인
  - 모든 사용자가 등록/수정/삭제 가능
  - Soft Delete로 삭제 후 조회 불가

**📝 Snake Case 필드명 매핑:**

- `companyId` → `company_id`
- `jobPostingId` → `job_posting_id`
- `companyName` → `company_name`
- `jobTitle` → `job_title`
- `isCurrent` → `is_current`
- `startDate` → `start_date`
- `endDate` → `end_date`
- `expYears` → `exp_years`
- `userId` → `user_id`
- `skillId` → `skill_id`

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
