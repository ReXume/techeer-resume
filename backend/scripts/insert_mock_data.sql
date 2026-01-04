-- ============================================
-- 대량 Mock Data 생성 스크립트 (간소화 버전)
-- 실 서비스 사례를 고려한 비율로 데이터 생성
-- 총 약 12,800개 이상의 레코드 생성
-- ============================================

SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';

-- ============================================
-- 1. Users (사용자) - 1000명
-- ============================================
INSERT INTO users (email, name, password, refresh_token, role, social_type, created_at, updated_at, deleted_at)
SELECT 
    CONCAT('user', n, '@example.com') as email,
    CONCAT('사용자', n) as name,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJZO' as password,
    NULL as refresh_token,
    CASE 
        WHEN n <= 10 THEN 'ADMIN'
        WHEN n <= 50 THEN 'PREMIUM'
        ELSE 'USER'
    END as role,
    CASE 
        WHEN n % 5 = 0 THEN 'LOCAL'
        WHEN n % 5 = 1 THEN 'GITHUB'
        WHEN n % 5 = 2 THEN 'GOOGLE'
        WHEN n % 5 = 3 THEN 'KAKAO'
        ELSE 'LINKEDIN'
    END as social_type,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3,
         (SELECT @row := 0) r
    LIMIT 1000
) numbers;

-- ============================================
-- 2. Companies (기업) - 150개
-- ============================================
INSERT INTO companies (name, industry_domain, website_url, location, created_at, updated_at, deleted_at)
SELECT 
    CONCAT('기업', n) as name,
    CASE 
        WHEN n % 10 = 0 THEN 'IT/소프트웨어'
        WHEN n % 10 = 1 THEN '금융/보험'
        WHEN n % 10 = 2 THEN '제조업'
        WHEN n % 10 = 3 THEN '유통/물류'
        WHEN n % 10 = 4 THEN '건설/부동산'
        WHEN n % 10 = 5 THEN '의료/바이오'
        WHEN n % 10 = 6 THEN '교육'
        WHEN n % 10 = 7 THEN '미디어/엔터테인먼트'
        WHEN n % 10 = 8 THEN '서비스업'
        ELSE '기타'
    END as industry_domain,
    CONCAT('https://company', n, '.com') as website_url,
    CASE 
        WHEN n % 5 = 0 THEN '서울특별시'
        WHEN n % 5 = 1 THEN '경기도'
        WHEN n % 5 = 2 THEN '부산광역시'
        WHEN n % 5 = 3 THEN '인천광역시'
        ELSE '대전광역시'
    END as location,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 730) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
         (SELECT @row := 0) r
    LIMIT 150
) numbers;

-- ============================================
-- 3. Company Members (기업 멤버) - 150개
-- ============================================
INSERT INTO company_members (user_id, company_id, role, status, created_at, updated_at, deleted_at)
SELECT 
    n as user_id,
    n as company_id,
    'ADMIN' as role,
    'ACTIVE' as status,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
         (SELECT @row := 0) r
    LIMIT 150
) numbers
WHERE n <= (SELECT COUNT(*) FROM companies);

-- ============================================
-- 4. Job Postings (채용공고) - 800개
-- ============================================
INSERT INTO job_postings (company_id, title, contents, exp_years, source_type, origin_url, status, created_at, updated_at, deleted_at)
SELECT 
    FLOOR(1 + RAND() * (SELECT COUNT(*) FROM companies)) as company_id,
    CASE 
        WHEN n % 20 = 0 THEN CONCAT('백엔드 개발자 채용 (', n, ')')
        WHEN n % 20 = 1 THEN CONCAT('프론트엔드 개발자 채용 (', n, ')')
        WHEN n % 20 = 2 THEN CONCAT('풀스택 개발자 채용 (', n, ')')
        WHEN n % 20 = 3 THEN CONCAT('데이터 엔지니어 채용 (', n, ')')
        WHEN n % 20 = 4 THEN CONCAT('DevOps 엔지니어 채용 (', n, ')')
        WHEN n % 20 = 5 THEN CONCAT('iOS 개발자 채용 (', n, ')')
        WHEN n % 20 = 6 THEN CONCAT('Android 개발자 채용 (', n, ')')
        WHEN n % 20 = 7 THEN CONCAT('QA 엔지니어 채용 (', n, ')')
        WHEN n % 20 = 8 THEN CONCAT('프로덕트 매니저 채용 (', n, ')')
        WHEN n % 20 = 9 THEN CONCAT('UI/UX 디자이너 채용 (', n, ')')
        WHEN n % 20 = 10 THEN CONCAT('마케팅 매니저 채용 (', n, ')')
        WHEN n % 20 = 11 THEN CONCAT('영업 매니저 채용 (', n, ')')
        WHEN n % 20 = 12 THEN CONCAT('인사 담당자 채용 (', n, ')')
        WHEN n % 20 = 13 THEN CONCAT('재무 담당자 채용 (', n, ')')
        WHEN n % 20 = 14 THEN CONCAT('기획자 채용 (', n, ')')
        WHEN n % 20 = 15 THEN CONCAT('시니어 개발자 채용 (', n, ')')
        WHEN n % 20 = 16 THEN CONCAT('주니어 개발자 채용 (', n, ')')
        WHEN n % 20 = 17 THEN CONCAT('시스템 엔지니어 채용 (', n, ')')
        WHEN n % 20 = 18 THEN CONCAT('보안 엔지니어 채용 (', n, ')')
        ELSE CONCAT('소프트웨어 엔지니어 채용 (', n, ')')
    END as title,
    CONCAT('우리 회사는 최고의 인재를 찾고 있습니다.\n',
           '주요 업무:\n',
           '- 신규 서비스 개발 및 운영\n',
           '- 기존 시스템 개선 및 최적화\n',
           '- 팀원들과의 협업\n\n',
           '자격 요건:\n',
           '- 관련 경력 ', FLOOR(1 + RAND() * 5), '년 이상\n',
           '- ', CASE WHEN n % 2 = 0 THEN 'Java' ELSE 'Python' END, ' 개발 경험\n',
           '- 협업 능력 및 소통 능력\n\n',
           '우대 사항:\n',
           '- 대규모 서비스 개발 경험\n',
           '- 클라우드 인프라 경험') as contents,
    FLOOR(RAND() * 7) as exp_years,
    CASE WHEN n % 3 = 0 THEN 'CRAWLED' ELSE 'DIRECT' END as source_type,
    CASE WHEN n % 3 = 0 THEN CONCAT('https://external-job-site.com/job/', n) ELSE NULL END as origin_url,
    CASE WHEN n % 10 = 0 THEN 'CLOSED' ELSE 'OPEN' END as status,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 180) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3,
         (SELECT @row := 0) r
    LIMIT 800
) numbers;

-- ============================================
-- 5. User Files (사용자 파일) - 2700개
-- ============================================
-- Resume Files (900개)
INSERT INTO user_files (user_id, category, uuid, file_url, file_type, original_name, created_at)
SELECT 
    FLOOR(1 + RAND() * (SELECT COUNT(*) FROM users)) as user_id,
    'RESUME' as category,
    REPLACE(UUID(), '-', '') as uuid,
    CONCAT('https://storage.googleapis.com/bucket/resume/resume_', n, '.pdf') as file_url,
    'PDF' as file_type,
    CONCAT('이력서_', n, '.pdf') as original_name,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
         (SELECT @row := 0) r
    LIMIT 900
) numbers;

-- Portfolio Files (700개)
INSERT INTO user_files (user_id, category, uuid, file_url, file_type, original_name, created_at)
SELECT 
    FLOOR(1 + RAND() * (SELECT COUNT(*) FROM users)) as user_id,
    'PORTFOLIO' as category,
    REPLACE(UUID(), '-', '') as uuid,
    CONCAT('https://storage.googleapis.com/bucket/portfolio/portfolio_', n, '.pdf') as file_url,
    'PDF' as file_type,
    CONCAT('포트폴리오_', n, '.pdf') as original_name,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
         (SELECT @row := 0) r
    LIMIT 700
) numbers;

-- Education Files (1100개)
INSERT INTO user_files (user_id, category, uuid, file_url, file_type, original_name, created_at)
SELECT 
    FLOOR(1 + RAND() * (SELECT COUNT(*) FROM users)) as user_id,
    'EDUCATION' as category,
    REPLACE(UUID(), '-', '') as uuid,
    CONCAT('https://storage.googleapis.com/bucket/education/education_', n, '.pdf') as file_url,
    'PDF' as file_type,
    CONCAT('학력증명서_', n, '.pdf') as original_name,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
         (SELECT @row := 0) r
    LIMIT 1100
) numbers;

-- ============================================
-- 6. Resumes (이력서) - 900개
-- ============================================
INSERT INTO resumes (file_id, title, is_default, created_at, updated_at, deleted_at)
SELECT 
    uf.file_id,
    CONCAT('이력서_', uf.file_id) as title,
    FALSE as is_default,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM user_files uf
WHERE uf.category = 'RESUME'
LIMIT 900;

-- 각 사용자의 첫 번째 이력서를 기본으로 설정
UPDATE resumes r
INNER JOIN (
    SELECT MIN(resume_id) as first_resume_id, file_id
    FROM resumes
    GROUP BY file_id
) first ON r.resume_id = first.first_resume_id
SET r.is_default = TRUE
WHERE r.resume_id IN (
    SELECT resume_id FROM (
        SELECT r2.resume_id
        FROM resumes r2
        INNER JOIN user_files uf ON r2.file_id = uf.file_id
        WHERE uf.category = 'RESUME'
        GROUP BY uf.user_id
        HAVING MIN(r2.resume_id)
    ) sub
);

-- ============================================
-- 7. Portfolios (포트폴리오) - 700개
-- ============================================
INSERT INTO portfolios (file_id, title, is_default, created_at, updated_at, deleted_at)
SELECT 
    uf.file_id,
    CONCAT('포트폴리오_', uf.file_id) as title,
    FALSE as is_default,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM user_files uf
WHERE uf.category = 'PORTFOLIO'
LIMIT 700;

-- 각 사용자의 첫 번째 포트폴리오를 기본으로 설정
UPDATE portfolios p
INNER JOIN (
    SELECT MIN(portfolio_id) as first_portfolio_id, file_id
    FROM portfolios
    GROUP BY file_id
) first ON p.portfolio_id = first.first_portfolio_id
SET p.is_default = TRUE
WHERE p.portfolio_id IN (
    SELECT portfolio_id FROM (
        SELECT p2.portfolio_id
        FROM portfolios p2
        INNER JOIN user_files uf ON p2.file_id = uf.file_id
        WHERE uf.category = 'PORTFOLIO'
        GROUP BY uf.user_id
        HAVING MIN(p2.portfolio_id)
    ) sub
);

-- ============================================
-- 8. Educations (학력) - 1100개
-- ============================================
INSERT INTO educations (file_id, title, is_default, created_at, updated_at, deleted_at)
SELECT 
    uf.file_id,
    CASE 
        WHEN n % 4 = 0 THEN '고등학교 졸업'
        WHEN n % 4 = 1 THEN '전문대학 졸업'
        WHEN n % 4 = 2 THEN '대학교 졸업'
        ELSE '대학원 졸업'
    END as title,
    CASE WHEN n % 2 = 0 THEN TRUE ELSE FALSE END as is_default,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM user_files uf
CROSS JOIN (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
         (SELECT @row := 0) r
    LIMIT 1100
) numbers
WHERE uf.category = 'EDUCATION'
LIMIT 1100;

-- ============================================
-- 9. User Careers (경력) - 1800개
-- ============================================
INSERT INTO user_careers (user_id, file_id, company_id, company_name, job_title, is_current, start_date, end_date, created_at, updated_at, deleted_at)
SELECT 
    FLOOR(1 + RAND() * (SELECT COUNT(*) FROM users)) as user_id,
    NULL as file_id,
    CASE WHEN n % 3 = 0 THEN FLOOR(1 + RAND() * (SELECT COUNT(*) FROM companies)) ELSE NULL END as company_id,
    CASE 
        WHEN n % 10 = 0 THEN '네이버'
        WHEN n % 10 = 1 THEN '카카오'
        WHEN n % 10 = 2 THEN '삼성전자'
        WHEN n % 10 = 3 THEN 'LG전자'
        WHEN n % 10 = 4 THEN 'SK하이닉스'
        WHEN n % 10 = 5 THEN '현대자동차'
        WHEN n % 10 = 6 THEN '토스'
        WHEN n % 10 = 7 THEN '당근마켓'
        WHEN n % 10 = 8 THEN '쿠팡'
        ELSE CONCAT('기업', FLOOR(1 + RAND() * 100))
    END as company_name,
    CASE 
        WHEN n % 8 = 0 THEN '백엔드 개발자'
        WHEN n % 8 = 1 THEN '프론트엔드 개발자'
        WHEN n % 8 = 2 THEN '풀스택 개발자'
        WHEN n % 8 = 3 THEN '데이터 엔지니어'
        WHEN n % 8 = 4 THEN 'DevOps 엔지니어'
        WHEN n % 8 = 5 THEN '시니어 개발자'
        WHEN n % 8 = 6 THEN '주니어 개발자'
        ELSE '소프트웨어 엔지니어'
    END as job_title,
    CASE WHEN n % 3 = 0 THEN TRUE ELSE FALSE END as is_current,
    DATE_SUB(NOW(), INTERVAL FLOOR(365 + RAND() * 1825) DAY) as start_date,
    CASE 
        WHEN n % 3 = 0 THEN NULL
        ELSE DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY)
    END as end_date,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3,
         (SELECT @row := 0) r
    LIMIT 1800
) numbers;

-- ============================================
-- 10. Applications (지원) - 2500개
-- ============================================
INSERT INTO applications (user_id, jobposting_id, status, created_at, updated_at, deleted_at)
SELECT 
    FLOOR(1 + RAND() * (SELECT COUNT(*) FROM users)) as user_id,
    FLOOR(1 + RAND() * (SELECT COUNT(*) FROM job_postings)) as jobposting_id,
    CASE 
        WHEN n % 10 = 0 THEN 'APPLIED'
        WHEN n % 10 = 1 THEN 'VIEWED'
        WHEN n % 10 = 2 THEN 'PASSED'
        WHEN n % 10 = 3 THEN 'REJECTED'
        WHEN n % 10 = 4 THEN 'APPLIED'
        WHEN n % 10 = 5 THEN 'VIEWED'
        WHEN n % 10 = 6 THEN 'APPLIED'
        WHEN n % 10 = 7 THEN 'VIEWED'
        WHEN n % 10 = 8 THEN 'PASSED'
        ELSE 'APPLIED'
    END as status,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3,
         (SELECT @row := 0) r
    LIMIT 2500
) numbers;

-- ============================================
-- 11. Bookmarks (북마크) - 2000개
-- ============================================
INSERT INTO bookmarks (user_id, jobposting_id, created_at, updated_at, deleted_at)
SELECT 
    user_id,
    jobposting_id,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 180) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM (
    SELECT DISTINCT
        FLOOR(1 + RAND() * (SELECT COUNT(*) FROM users)) as user_id,
        FLOOR(1 + RAND() * (SELECT COUNT(*) FROM job_postings)) as jobposting_id
    FROM (
        SELECT @row := @row + 1 as n
        FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
             (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
             (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3,
             (SELECT @row := 0) r
        LIMIT 2500
    ) numbers
) unique_pairs
WHERE NOT EXISTS (
    SELECT 1 FROM bookmarks b 
    WHERE b.user_id = unique_pairs.user_id
      AND b.jobposting_id = unique_pairs.jobposting_id
)
LIMIT 2000;

-- ============================================
-- 12. Company Likes (기업 좋아요) - 1200개
-- ============================================
INSERT INTO company_likes (user_id, company_id, created_at, updated_at, deleted_at)
SELECT 
    user_id,
    company_id,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM (
    SELECT DISTINCT
        FLOOR(1 + RAND() * (SELECT COUNT(*) FROM users)) as user_id,
        FLOOR(1 + RAND() * (SELECT COUNT(*) FROM companies)) as company_id
    FROM (
        SELECT @row := @row + 1 as n
        FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
             (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3,
             (SELECT @row := 0) r
        LIMIT 1500
    ) numbers
) unique_pairs
WHERE NOT EXISTS (
    SELECT 1 FROM company_likes cl 
    WHERE cl.user_id = unique_pairs.user_id
      AND cl.company_id = unique_pairs.company_id
)
LIMIT 1200;

-- ============================================
-- 13. User Skills (사용자 스킬) - 2500개
-- Skill이 먼저 생성되어 있어야 함
-- ============================================
INSERT INTO user_skills (user_id, skill_id, created_at, updated_at, deleted_at)
SELECT 
    user_id,
    skill_id,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as updated_at,
    NULL as deleted_at
FROM (
    SELECT DISTINCT
        FLOOR(1 + RAND() * (SELECT COUNT(*) FROM users)) as user_id,
        (SELECT skill_id FROM skills WHERE deleted_at IS NULL ORDER BY RAND() LIMIT 1) as skill_id
    FROM (
        SELECT @row := @row + 1 as n
        FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
             (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
             (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3,
             (SELECT @row := 0) r
        LIMIT 3000
    ) numbers
    WHERE EXISTS (SELECT 1 FROM skills WHERE deleted_at IS NULL)
) unique_pairs
WHERE NOT EXISTS (
    SELECT 1 FROM user_skills us 
    WHERE us.user_id = unique_pairs.user_id
      AND us.skill_id = unique_pairs.skill_id
)
LIMIT 2500;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 데이터 생성 완료 통계
-- ============================================
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

