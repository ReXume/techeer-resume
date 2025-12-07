package com.techeer.backend.integration.document;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techeer.backend.api.document.dto.request.EducationCreateRequest;
import com.techeer.backend.api.document.dto.request.PortfolioCreateRequest;
import com.techeer.backend.api.document.dto.request.ResumeCreateRequest;
import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.config.FakeGcsServerTestConfig;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Testcontainers
@Import(FakeGcsServerTestConfig.class)
@ActiveProfiles("test")
class DocumentApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("test@example.com")
            .name("TestUser")
            .password("password")
            .role(Role.USER)
            .socialType(SocialType.LOCAL)
            .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("이력서 등록 성공 테스트")
    void createResume_Success() throws Exception {
        // given
        ResumeCreateRequest request = new ResumeCreateRequest(user.getId(), "My Resume", true);
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", "dummy content".getBytes());
        MockMultipartFile requestPart = new MockMultipartFile("request", "request.json", "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        // when & then
        mockMvc.perform(multipart("/api/v1/resumes")
                .file(file)
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("포트폴리오 등록 성공 테스트")
    void createPortfolio_Success() throws Exception {
        // given
        PortfolioCreateRequest request = new PortfolioCreateRequest(user.getId(), "My Portfolio", false);
        MockMultipartFile file = new MockMultipartFile("file", "portfolio.pdf", "application/pdf", "dummy content".getBytes());
        MockMultipartFile requestPart = new MockMultipartFile("request", "request.json", "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        // when & then
        mockMvc.perform(multipart("/api/v1/portfolios")
                .file(file)
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("학력 등록 성공 테스트")
    void createEducation_Success() throws Exception {
        // given
        EducationCreateRequest request = new EducationCreateRequest(user.getId(), "My Education", false);
        MockMultipartFile file = new MockMultipartFile("file", "degree.pdf", "application/pdf", "dummy content".getBytes());
        MockMultipartFile requestPart = new MockMultipartFile("request", "request.json", "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        // when & then
        mockMvc.perform(multipart("/api/v1/educations")
                .file(file)
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
    }
}
