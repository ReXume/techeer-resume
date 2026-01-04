# Mock Data 생성 가이드

대량 Mock Data를 생성하여 테스트 환경을 구축하는 방법을 안내합니다.

## 개요

`backend/scripts/insert_mock_data.sql` 스크립트를 사용하여 실 서비스 사례를 고려한 비율로 약 13,000개 이상의 레코드를 생성할 수 있습니다.

### 생성되는 데이터 규모

| 도메인          | 레코드 수       | 비고                                       |
| --------------- | --------------- | ------------------------------------------ |
| Users           | 1,000명         | ADMIN 10명, PREMIUM 40명, USER 950명       |
| Companies       | 150개           | 다양한 산업 분야                           |
| Company Members | 150개           | 각 기업당 1명 관리자                       |
| Job Postings    | 800개           | 기업당 평균 5-6개                          |
| User Files      | 2,700개         | Resume 900, Portfolio 700, Education 1,100 |
| Resumes         | 900개           | 각 사용자의 첫 번째 이력서는 기본값        |
| Portfolios      | 700개           | 각 사용자의 첫 번째 포트폴리오는 기본값    |
| Educations      | 1,100개         | 다양한 학력 정보                           |
| User Careers    | 1,800개         | 사용자당 평균 1.8개 경력                   |
| Applications    | 2,500개         | 사용자당 평균 2.5개 지원                   |
| Bookmarks       | 2,000개         | 사용자당 평균 2개 북마크                   |
| Company Likes   | 1,200개         | 사용자당 평균 1.2개 좋아요                 |
| User Skills     | 2,500개         | 사용자당 평균 2.5개 스킬                   |
| **총합**        | **약 13,000개** | Skills 제외                                |

## 사전 준비

### 1. Skills 데이터 확인

`user_skills` 테이블에 데이터를 생성하기 위해서는 `skills` 테이블에 최소 1개 이상의 데이터가 있어야 합니다.

```sql
-- Skills 테이블 확인
SELECT COUNT(*) FROM skills WHERE deleted_at IS NULL;
```

Skills 데이터가 없는 경우, 먼저 Skills를 생성해야 합니다. API를 통해 생성하거나 직접 SQL로 삽입할 수 있습니다.

```sql
-- 예시: 기본 Skills 데이터 삽입
INSERT INTO skills (name, created_at, updated_at, deleted_at)
VALUES
    ('Java', NOW(), NOW(), NULL),
    ('Python', NOW(), NOW(), NULL),
    ('JavaScript', NOW(), NOW(), NULL),
    ('Spring Boot', NOW(), NOW(), NULL),
    ('React', NOW(), NOW(), NULL);
```

## 실행 방법

### 방법 1: Docker 컨테이너를 통한 실행 (권장)

Docker Compose로 실행 중인 MySQL 컨테이너에 직접 스크립트를 실행합니다.

```bash
# 프로젝트 루트 디렉토리에서 실행
docker exec -i techeer-resume-mysql-1 mysql -uroot -proot techeer < backend/scripts/insert_mock_data.sql
```

**컨테이너 이름 확인:**

```bash
# 실행 중인 MySQL 컨테이너 확인
docker ps | grep mysql
```

컨테이너 이름이 다른 경우, 위 명령어의 `techeer-resume-mysql-1` 부분을 실제 컨테이너 이름으로 변경하세요.

### 방법 2: MySQL 클라이언트를 통한 실행

로컬에 MySQL 클라이언트가 설치되어 있고, 데이터베이스에 직접 접근할 수 있는 경우:

```bash
# 프로젝트 루트 디렉토리에서 실행
mysql -uroot -proot -h127.0.0.1 -P3306 techeer < backend/scripts/insert_mock_data.sql
```

**연결 정보가 다른 경우:**

```bash
# 호스트, 포트, 사용자명, 비밀번호, 데이터베이스명을 실제 값으로 변경
mysql -u[사용자명] -p[비밀번호] -h[호스트] -P[포트] [데이터베이스명] < backend/scripts/insert_mock_data.sql
```

### 방법 3: MySQL 클라이언트에서 직접 실행

MySQL 클라이언트(MySQL Workbench, DBeaver, TablePlus 등)에서 스크립트를 열어 직접 실행할 수 있습니다.

1. MySQL 클라이언트에서 `techeer` 데이터베이스에 연결
2. `backend/scripts/insert_mock_data.sql` 파일을 열기
3. 전체 스크립트를 선택하여 실행

## 실행 결과 확인

스크립트 실행이 완료되면 자동으로 각 테이블의 레코드 수를 조회하는 통계 쿼리가 실행됩니다.

```sql
-- 수동으로 통계 확인 (스크립트 마지막 부분에 포함됨)
SELECT
    'Users' as table_name, COUNT(*) as record_count FROM users WHERE deleted_at IS NULL
UNION ALL
SELECT 'Companies', COUNT(*) FROM companies WHERE deleted_at IS NULL
UNION ALL
SELECT 'Company Members', COUNT(*) FROM company_members WHERE deleted_at IS NULL
UNION ALL
SELECT 'Job Postings', COUNT(*) FROM job_postings WHERE deleted_at IS NULL
UNION ALL
SELECT 'Applications', COUNT(*) FROM applications WHERE deleted_at IS NULL
UNION ALL
SELECT 'Bookmarks', COUNT(*) FROM bookmarks WHERE deleted_at IS NULL
UNION ALL
SELECT 'Company Likes', COUNT(*) FROM company_likes WHERE deleted_at IS NULL
UNION ALL
SELECT 'User Careers', COUNT(*) FROM user_careers WHERE deleted_at IS NULL
UNION ALL
SELECT 'User Skills', COUNT(*) FROM user_skills WHERE deleted_at IS NULL
UNION ALL
SELECT 'Resumes', COUNT(*) FROM resumes WHERE deleted_at IS NULL
UNION ALL
SELECT 'Portfolios', COUNT(*) FROM portfolios WHERE deleted_at IS NULL
UNION ALL
SELECT 'Educations', COUNT(*) FROM educations WHERE deleted_at IS NULL
UNION ALL
SELECT 'User Files', COUNT(*) FROM user_files
ORDER BY table_name;
```

## 주의사항

### 1. 기존 데이터 보존

스크립트는 기존 데이터를 삭제하지 않고 추가만 합니다. 하지만 다음 테이블의 경우 중복 방지 로직이 있어 일부 레코드가 생성되지 않을 수 있습니다:

- `bookmarks` (user_id, jobposting_id 중복 방지)
- `company_likes` (user_id, company_id 중복 방지)
- `user_skills` (user_id, skill_id 중복 방지)

### 2. 실행 시간

대량 데이터 생성이므로 실행 시간이 다소 걸릴 수 있습니다 (약 1-2분).

### 3. 외래키 제약 조건

스크립트 실행 중에는 `FOREIGN_KEY_CHECKS`가 비활성화되어 있습니다. 실행 완료 후 자동으로 다시 활성화됩니다.

### 4. 트랜잭션

스크립트는 트랜잭션으로 묶여있지 않습니다. 중간에 오류가 발생하면 부분적으로 데이터가 생성될 수 있습니다. 필요시 수동으로 롤백하거나 데이터를 정리해야 합니다.

## 문제 해결

### 오류: "Table 'techeer.skills' doesn't exist"

Skills 테이블이 없는 경우, 먼저 애플리케이션을 실행하여 테이블을 생성하거나 마이그레이션을 실행하세요.

### 오류: "Duplicate entry for key"

중복 방지 로직이 작동하여 일부 레코드가 생성되지 않을 수 있습니다. 이는 정상적인 동작입니다.

### 오류: "Cannot add or update a child row: a foreign key constraint fails"

외래키 제약 조건 오류가 발생한 경우:

1. 스크립트가 올바른 순서로 실행되었는지 확인
2. 참조되는 테이블에 데이터가 존재하는지 확인
3. `FOREIGN_KEY_CHECKS`가 비활성화되어 있는지 확인

## 데이터 초기화

기존 데이터를 모두 삭제하고 새로 시작하려면:

```sql
-- 주의: 모든 데이터가 삭제됩니다!
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE user_skills;
TRUNCATE TABLE educations;
TRUNCATE TABLE portfolios;
TRUNCATE TABLE resumes;
TRUNCATE TABLE user_careers;
TRUNCATE TABLE company_likes;
TRUNCATE TABLE bookmarks;
TRUNCATE TABLE applications;
TRUNCATE TABLE job_postings;
TRUNCATE TABLE company_members;
TRUNCATE TABLE user_files;
TRUNCATE TABLE companies;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;
```

그 후 스크립트를 다시 실행하세요.

## 추가 정보

- 스크립트 파일 위치: `backend/scripts/insert_mock_data.sql`
- 생성되는 데이터는 테스트 목적으로만 사용하세요
- 프로덕션 환경에서는 절대 실행하지 마세요
