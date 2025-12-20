# 이메일 인증 기능 구현 TODO

## 📋 개요

사용자 회원가입과 기업 등록 시 이메일 인증 기능이 필요합니다.
이는 LinkedIn, Indeed 등 주요 채용 플랫폼에서 사용하는 표준 검증 방식입니다.

---

## 🎯 필요한 곳

### 1. 유저 회원가입 (User Registration)

**목적:** 실제 사용자의 이메일 주소 확인 및 계정 활성화

**현재 상태:**

- 이메일 입력만으로 회원가입 가능
- 이메일 소유권 검증 없음

**개선 필요사항:**

```
Step 1: 회원가입 정보 입력 (이메일, 비밀번호 등)
        → User status = PENDING

Step 2: 인증 이메일 발송
        → 6자리 인증 코드 or 인증 링크

Step 3: 이메일 인증 완료
        → User status = ACTIVE
        → 로그인 가능
```

**관련 파일:**

- `backend/src/main/java/com/techeer/backend/api/user/adapter/in/web/UserController.java`
- `backend/src/main/java/com/techeer/backend/api/user/domain/User.java`

---

### 2. 기업 등록 (Company Registration)

**목적:** 회사 도메인 이메일 소유 여부 확인 및 관리자 권한 검증

**현재 상태:**

- 로그인한 모든 사용자가 기업 등록 가능
- 회사 이메일 검증 없음
- 첫 등록자가 자동으로 ADMIN 권한 획득

**개선 필요사항:**

```
Step 1: 기업 정보 + 회사 이메일 입력
        → Company status = PENDING
        → CompanyMember status = PENDING

Step 2: 회사 이메일로 인증 코드 발송
        → 이메일 도메인이 websiteUrl과 매칭되는지 검증
        → 예: admin@techeer.net ↔ https://techeer.net

Step 3: 이메일 인증 완료
        → Company status = VERIFIED
        → CompanyMember status = ACTIVE, role = ADMIN
```

**검증 로직:**

- 이메일 도메인과 회사 웹사이트 도메인 일치 여부 확인
- 같은 도메인 이메일을 가진 사용자는 기존 기업에 멤버로 추가 가능
- 중복 기업 등록 방지

**관련 파일:**

- `backend/src/main/java/com/techeer/backend/api/company/adapter/in/web/CompanyController.java`
- `backend/src/main/java/com/techeer/backend/api/company/domain/Company.java`
- `backend/src/main/java/com/techeer/backend/api/company/domain/CompanyMember.java`

---

## 🛠️ 구현 요구사항

### 1. 인증 코드 생성 및 관리

```java
// EmailVerification Entity (새로 추가 필요)
@Entity
public class EmailVerification {
    @Id
    private Long id;

    private String email;
    private String verificationCode; // 6자리 랜덤 숫자
    private LocalDateTime expiresAt; // 유효기간 (5분 or 10분)
    private boolean verified;
    private String type; // USER_REGISTRATION, COMPANY_REGISTRATION
}
```

### 2. 이메일 발송 서비스

```java
public interface EmailService {
    // 인증 코드 발송
    void sendVerificationCode(String email, String code);

    // 인증 링크 발송 (선택적)
    void sendVerificationLink(String email, String token);
}
```

**구현 방법 옵션:**

- **Spring Mail** (SMTP 사용)
- **AWS SES** (Simple Email Service)
- **SendGrid** (3rd party 서비스)
- **Mailgun** (3rd party 서비스)

### 3. 인증 API 엔드포인트

```java
// 인증 코드 발송
POST /api/v1/auth/email/send-verification
Request: { "email": "user@example.com", "type": "USER_REGISTRATION" }

// 인증 코드 확인
POST /api/v1/auth/email/verify
Request: { "email": "user@example.com", "code": "123456" }

// 인증 코드 재발송
POST /api/v1/auth/email/resend
Request: { "email": "user@example.com" }
```

### 4. User/Company 엔티티 수정

```java
// User Entity에 추가
@Enumerated(EnumType.STRING)
private UserStatus status; // PENDING, ACTIVE, INACTIVE

private boolean emailVerified;
private LocalDateTime emailVerifiedAt;

// Company Entity에 추가
@Enumerated(EnumType.STRING)
private CompanyStatus status; // PENDING, VERIFIED, REJECTED

private String verifiedEmail; // 검증된 회사 이메일
private LocalDateTime verifiedAt;
```

---

## 📝 구현 순서 (권장)

### Phase 1: 기반 구조

1. ✅ EmailVerification 엔티티 생성
2. ✅ EmailVerification Repository 생성
3. ✅ 인증 코드 생성 유틸리티 작성

### Phase 2: 이메일 발송

4. ⬜ EmailService 인터페이스 및 구현체 작성
5. ⬜ 이메일 템플릿 작성 (HTML)
6. ⬜ SMTP 설정 (application.yml)

### Phase 3: API 구현

7. ⬜ 인증 코드 발송 API
8. ⬜ 인증 코드 검증 API
9. ⬜ 인증 코드 재발송 API

### Phase 4: 통합

10. ⬜ 회원가입 프로세스에 이메일 인증 추가
11. ⬜ 기업 등록 프로세스에 이메일 인증 추가
12. ⬜ 로그인 시 이메일 인증 여부 체크

---

## ⚠️ 주의사항

### 보안

- ✅ 인증 코드는 **6자리 랜덤 숫자** 권장
- ✅ 유효기간 설정 필수 (5분 or 10분)
- ✅ 인증 시도 횟수 제한 (5회 실패 시 재발송 요구)
- ✅ 인증 코드는 **해시 저장** 권장 (평문 저장 금지)
- ✅ Rate Limiting 적용 (동일 이메일로 1분에 1회만 발송)

### UX

- 이메일이 도착하지 않을 경우 "재발송" 버튼 제공
- 인증 코드 입력 시 남은 유효시간 표시
- 인증 완료 후 자동 로그인 or 로그인 페이지로 리다이렉트

### 운영

- 이메일 발송 실패 시 로그 기록
- 스팸 메일함에 가지 않도록 SPF, DKIM 설정
- 발송량 모니터링 및 알림

---

## 📚 참고 자료

### 이메일 발송 라이브러리

- [Spring Boot Mail](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.email)
- [AWS SES Java SDK](https://docs.aws.amazon.com/ses/latest/dg/send-using-sdk-java.html)
- [SendGrid Java](https://github.com/sendgrid/sendgrid-java)

### 인증 코드 생성

```java
// 6자리 랜덤 숫자 생성
public String generateVerificationCode() {
    Random random = new Random();
    return String.format("%06d", random.nextInt(1000000));
}

// UUID 기반 토큰 생성 (링크 방식)
public String generateVerificationToken() {
    return UUID.randomUUID().toString();
}
```

### 이메일 템플릿 예시

```html
<!DOCTYPE html>
<html>
  <body>
    <h2>이메일 인증</h2>
    <p>안녕하세요, {userName}님!</p>
    <p>아래 인증 코드를 입력해주세요:</p>
    <h1 style="color: #4CAF50;">{verificationCode}</h1>
    <p>이 코드는 10분간 유효합니다.</p>
  </body>
</html>
```

---

## 🔗 관련 Issue

- [ ] #XX: 이메일 인증 인프라 구축 (SMTP 설정)
- [ ] #XX: EmailVerification 엔티티 및 Repository 구현
- [ ] #XX: 인증 코드 발송 API 구현
- [ ] #XX: 인증 코드 검증 API 구현
- [ ] #XX: 회원가입에 이메일 인증 통합
- [ ] #XX: 기업 등록에 회사 이메일 인증 통합

---

## ✅ 현재 구현 상태 (이메일 인증 제외)

이메일 인증 기능을 제외하고 구현 가능한 부분:

### Company 등록 개선 (구현 완료)

- ✅ `companyEmail` 필드 추가
- ✅ 이메일 도메인과 웹사이트 도메인 매칭 검증
- ✅ 중복 기업 등록 방지 강화
- ✅ CompanyMember 상태 관리 (PENDING → ACTIVE)

### 남은 작업

- ⬜ 실제 이메일 발송 기능 구현
- ⬜ EmailVerification 테이블 생성 및 관리
- ⬜ 인증 코드 생성 및 검증 로직
- ⬜ User 회원가입에 이메일 인증 추가
