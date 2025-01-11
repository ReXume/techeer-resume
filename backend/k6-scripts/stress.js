import http from 'k6/http'
import { check } from 'k6'
import { randomBytes } from 'k6/crypto'
import { sleep } from 'k6'

function createResume() {
  // PDF 파일 대신 더미 바이트
  let dummyPdf = randomBytes(10, 'base64')

  // 이력서 DTO(JSON)
  let resumeRequest = {
    // Controller에서 CreateResumeRequest가 요구하는 필드들
    // 예: { username, career, position, companyNames, techStackNames, ... }
    // 주어진 예시에서는 "CreateResumeRequest createResumeReq"
    username: 'k6user',
    position: 'BACKEND',
    career: 3,
    companyNames: ['NAVER', 'KAKAO'],
    techStackNames: ['JAVA', 'SPRING']
  }

  // multipart/form-data 형태
  // 1) key = 'resume'        → Controller의 @RequestPart("resume") CreateResumeRequest
  // 2) key = 'resume_file'   → Controller의 @RequestPart(name = "resume_file") MultipartFile
  let formData = {
    resume: JSON.stringify(resumeRequest),
    resume_file: http.file(dummyPdf, 'dummy.pdf', 'application/pdf')
  }

  // 만약 애플리케이션이 쿠키 기반이라면, 쿠키에 토큰을 넣어야 할 수도 있음
  // 예: params.cookies = { 'Access-Token': '...' };
  // (단, 코드/로직에 따라 다름, 헤더 기반이라면 Authorization 헤더로)

  let params = {
    tags: { endpoint: 'resume-create' },
    // cookie 기반 예시 (쿠키 이름은 예시)
    cookies: {
      'accessToken': 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsImV4cCI6MTczNjYwNDA2MiwiZW1haWwiOiJwYXNzaml3b24xMDA5QGdtYWlsLmNvbSJ9.ZYwaK9uMtiWqZAO3Z6tgHVEXpqS4toEp4NE67Aq30eQ'
    }
  }

  let url = 'http://backend:8080/api/v1/resumes'
  let res = http.post(url, formData, params)

  check(res, {
    '이력서 등록 201': (r) => r.status === 201
  })
}

export let options = {
  scenarios: {
    one_scenario: {
      executor: 'constant-vus',
      vus: 1,
      duration: '10s'
    }
  }
}

export default function () {
  createResume()
  sleep(1)
}
