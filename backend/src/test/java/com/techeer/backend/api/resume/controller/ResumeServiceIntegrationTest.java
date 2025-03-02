package com.techeer.backend.api.resume.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.dto.request.CreateResumeRequest;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.api.resume.service.ResumeService;
import com.techeer.backend.api.resume.service.facade.ResumeCreateFacade;
import com.techeer.backend.api.tag.position.Position;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.infra.aws.S3Uploader;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ResumeServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ResumeCreateFacade resumeCreateFacade;

    @Autowired
    private UserService userService;

    @MockBean
    private S3Uploader s3Uploader;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // 필요 시 다른 리소스 초기화
    }

    /**
     * [테스트 1] 이력서 생성 통합 테스트 - 쿠키 기반 JWT & @RequestPart(JSON + 파일) 구조 - MockMvc를 통해 실제 API 엔드포인트(/api/v1/resumes)를 호출하여 테스트
     */
    @Test
    @DisplayName("이력서 생성 통합 테스트 - 쿠키 기반 JWT & @RequestPart(JSON+파일)")
    void createResume_ShouldCreateResume() throws Exception {

        // -----------------------------
        // Given
        // -----------------------------
        // 1) /api/v1/mock/signup으로 회원 가입 + Access Token 획득
        String userId = "john_doe@gmail.com";
        MvcResult signupResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/mock/signup")
                                .param("id", userId)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String signupResponse = signupResult.getResponse().getContentAsString();
        JsonNode signupJson = objectMapper.readTree(signupResponse);

        // 응답에서 JWT 문자열 추출(예: 필드명 "result")
        String accessToken = signupJson.get("result").asText();

        // 2) 이력서 생성 요청 DTO 준비
        CreateResumeRequest createResumeRequest = CreateResumeRequest.builder()
                .position(Position.BACKEND)
                .career(3)
                .techStackNames(List.of("Java", "Spring"))
                .companyNames(List.of("TechCompany"))
                .build();

        // 3) JSON 데이터를 멀티파트 파트 "resume"로 보낼 준비
        MockMultipartFile resumeJsonPart = new MockMultipartFile(
                "resume",
                "",                              // 파일 이름은 비어도 무방
                "application/json",              // JSON 콘텐츠 타입
                objectMapper.writeValueAsBytes(createResumeRequest) // JSON 직렬화
        );

        // 4) 업로드할 PDF 파일(파트 이름 "resume_file")
        MockMultipartFile resumeFile = new MockMultipartFile(
                "resume_file",
                "resume.pdf",
                "application/pdf",
                new byte[1024] // 1KB짜리 PDF
        );

        // 5) S3 업로더 Mock 설정
        given(s3Uploader.uploadPdf(any(MultipartFile.class)))
                .willReturn("https://s3.bucket.com/resume.pdf");

        // 6) 쿠키 설정 (JwtAuthenticationFilter가 이 쿠키를 확인)
        Cookie jwtCookie = new Cookie("accessToken", accessToken);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);

        // -----------------------------
        // When
        // -----------------------------
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/resumes")
                        // "resume" JSON 파트
                        .file(resumeJsonPart)
                        // "resume_file" PDF 파트
                        .file(resumeFile)
                        .cookie(jwtCookie)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // -----------------------------
        // Then
        // -----------------------------
        // 1) 응답 메시지 검증
        String responseJson = result.getResponse().getContentAsString();
        assertThat(responseJson).contains("RESUME_201");

        // 2) DB 상태 검증:
        // 이메일 기준으로 User 찾기 (mockSignup 과정에서 저장됨)
        User foundUser = userRepository.findByEmail(userId).orElse(null);
        assertNotNull(foundUser);

        // resumeService가 최종적으로 저장한 이력서를 꺼내 확인
        Resume savedResume = resumeService.findLaterByUser(foundUser);
        assertNotNull(savedResume);
        assertEquals(Position.BACKEND, savedResume.getPosition());
        assertEquals(3, savedResume.getCareer());
        assertEquals(foundUser.getUsername(), savedResume.getUser().getUsername());
        assertEquals("https://s3.bucket.com/resume.pdf", savedResume.getResumePdf().getPdf().getPdfUrl());

        // 추가 검증 (TechStack, Company 등)
        // assertTrue(savedResume.getTechStackNames().contains("Java"));
        // assertTrue(savedResume.getCompanyNames().contains("TechCompany"));
    }
}
