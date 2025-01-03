package com.techeer.backend.api.resume.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techeer.backend.api.feedback.dto.request.FeedbackCreateRequest;
import com.techeer.backend.api.feedback.repository.FeedbackRepository;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.api.tag.position.Position;
import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.global.jwt.service.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private EntityManager em;

    // JwtService 주입
    @Autowired
    private JwtService jwtService;

    private User testUser;
    private Resume testResume;
    private String accessToken; // 생성된 토큰을 저장할 변수

    @BeforeEach
    void setUp() {
        // Given: 테스트용 User와 Resume를 DB에 저장
        testUser = User.builder()
                .email("john@example.com")
                .username("JohnDoe")
                .role(Role.TECHEER)
                .socialType(SocialType.GOOGLE)
                .build();
        userRepository.save(testUser);

        testResume = Resume.builder()
                .user(testUser)
                .name("John's Resume")
                .career(5)
                .position(Position.DEVOPS)
                .build();
        resumeRepository.save(testResume);

        em.flush();
        em.clear();

        // JWT 토큰 생성 - User 객체 대신 이메일 전달
        accessToken = jwtService.createAccessToken(testUser.getEmail());
    }

    @Nested
    @DisplayName("POST /api/v1/resumes/{resume_id}/feedbacks")
    class CreateFeedback {

        @Test
        @DisplayName("Given 유효한 피드백 데이터, When 피드백 생성 요청, Then 201 상태코드와 생성된 피드백 반환")
        void createFeedback_Success() throws Exception {
            // Given: 유효한 요청 바디
            Long resumeId = testResume.getId();
            FeedbackCreateRequest request = new FeedbackCreateRequest(
                    "This is feedback",
                    100.5,
                    200.5,
                    null,
                    null,
                    1
            );

            // When: POST 요청에 JWT 토큰을 포함
            mockMvc.perform(post("/api/v1/resumes/{resume_id}/feedbacks", resumeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .cookie(new Cookie("accessToken", accessToken))) // JWT 토큰을 쿠키에 추가
                    // Then: 기대한 응답 상태/필드 검증
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.http_status").value("CREATED"))
                    .andExpect(jsonPath("$.code").value("COMMON_201"))
                    .andExpect(jsonPath("$.message").value("성공적으로 생성되었습니다."))
                    .andExpect(jsonPath("$.result.feedback_id").exists())
                    .andExpect(jsonPath("$.result.content").value("This is feedback"))
                    .andExpect(jsonPath("$.result.xcoordinate").value(100.5))
                    .andExpect(jsonPath("$.result.ycoordinate").value(200.5))
                    .andExpect(jsonPath("$.result.page_number").value(1));
        }

        @Test
        @DisplayName("Given 잘못된 피드백 데이터, When 피드백 생성 요청, Then 400 상태코드 반환")
        void createFeedback_InvalidData() throws Exception {
            // Given: content가 비어있는 유효하지 않은 요청
            Long resumeId = testResume.getId();
            FeedbackCreateRequest invalidRequest = new FeedbackCreateRequest(
                    "",
                    100.0,
                    200.0,
                    null,
                    null,
                    1
            );

            // When: POST 요청에 JWT 토큰을 포함
            mockMvc.perform(post("/api/v1/resumes/{resume_id}/feedbacks", resumeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest))
                            .cookie(new Cookie("accessToken", accessToken))) // JWT 토큰을 쿠키에 추가
                    // Then: 400 Bad Request 기대
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.http_status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.code").value("COMMON_400")) // 실제 예외 코드에 맞게 수정
                    .andExpect(jsonPath("$.error_message").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.errors[0].field").value("content"))
                    .andExpect(jsonPath("$.errors[0].value").value(""))
                    .andExpect(jsonPath("$.errors[0].reason").value("content는 필수입니다."));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/resumes/{resume_id}/feedbacks")
    class GetFeedbacks {

        @Test
        @DisplayName("Given 일반 피드백 존재, When 피드백 조회 요청, Then 200 상태코드와 피드백 리스트 반환")
        void getFeedbackWithAIFeedback_Success() throws Exception {
            // Given: 이력서에 피드백 2개 저장
            Long resumeId = testResume.getId();
            feedbackRepository.saveAll(List.of(
                    com.techeer.backend.api.feedback.domain.Feedback.builder()
                            .content("Good structure")
                            .xCoordinate(50.0)
                            .yCoordinate(60.0)
                            .pageNumber(1)
                            .user(testUser)
                            .resume(testResume)
                            .build(),
                    com.techeer.backend.api.feedback.domain.Feedback.builder()
                            .content("Improve formatting")
                            .xCoordinate(70.0)
                            .yCoordinate(80.0)
                            .pageNumber(2)
                            .user(testUser)
                            .resume(testResume)
                            .build()
            ));
            em.flush();
            em.clear();

            // When: GET 요청에 JWT 토큰을 포함
            mockMvc.perform(get("/api/v1/resumes/{resume_id}/feedbacks", resumeId)
                            .cookie(new Cookie("accessToken", accessToken))) // JWT 토큰을 쿠키에 추가
                    // Then: 200 OK와 피드백 2개 반환 검증
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.http_status").value("OK"))
                    .andExpect(jsonPath("$.code").value("FEEDBACK_200"))
                    .andExpect(jsonPath("$.message").value("피드백 조회 성공"))
                    .andExpect(jsonPath("$.result.feedback_responses.length()").value(2));
        }

        @Test
        @DisplayName("Given 피드백 없음, When 피드백 조회 요청, Then 200 상태코드와 빈 리스트 반환")
        void getFeedbackWithAIFeedback_Empty() throws Exception {
            // Given: 피드백 없는 상태
            Long resumeId = testResume.getId(); // 하드코딩된 ID

            // When: GET 요청에 JWT 토큰을 포함 (아무 피드백도 없음)
            mockMvc.perform(get("/api/v1/resumes/{resume_id}/feedbacks", resumeId)
                            .cookie(new Cookie("accessToken", accessToken))) // JWT 토큰을 쿠키에 추가
                    // Then: 200 OK 기대
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.http_status").value("OK"))
                    .andExpect(jsonPath("$.code").value("FEEDBACK_200"))
                    .andExpect(jsonPath("$.message").value("피드백 조회 성공"))
                    .andExpect(jsonPath("$.result.feedback_responses.length()").value(0));
        }

    }

    @Nested
    @DisplayName("DELETE /api/v1/resumes/{resume_id}/feedbacks/{feedback_id}")
    class DeleteFeedback {

        @Test
        @DisplayName("Given 삭제 대상 피드백 존재, When 삭제 요청, Then 204 NO_CONTENT 반환")
        void deleteFeedback_Success() throws Exception {
            // Given: 피드백 1개 저장
            Long resumeId = testResume.getId();
            var feedback = feedbackRepository.save(
                    com.techeer.backend.api.feedback.domain.Feedback.builder()
                            .content("To delete")
                            .xCoordinate(10.0)
                            .yCoordinate(20.0)
                            .pageNumber(1)
                            .user(testUser)
                            .resume(testResume)
                            .build()
            );
            em.flush();
            em.clear();

            // When: DELETE 요청에 JWT 토큰을 포함
            mockMvc.perform(delete("/api/v1/resumes/{resume_id}/feedbacks/{feedback_id}",
                            resumeId, feedback.getId())
                            .cookie(new Cookie("accessToken", accessToken))) // JWT 토큰을 쿠키에 추가
                    // Then: 성공적으로 삭제되며 204 반환
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Given 존재하지 않는 피드백, When 삭제 요청, Then 404 NOT_FOUND 반환")
        void deleteFeedback_NotFound() throws Exception {
            // Given: 존재하지 않는 feedback_id
            Long resumeId = testResume.getId();

            // When: DELETE 요청에 JWT 토큰을 포함
            mockMvc.perform(delete("/api/v1/resumes/{resume_id}/feedbacks/{feedback_id}",
                            resumeId, 99999L)
                            .cookie(new Cookie("accessToken", accessToken))) // JWT 토큰을 쿠키에 추가
                    // Then: 404 NOT_FOUND 반환
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.http_status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.code").value("FEEDBACK_404"))
                    .andExpect(jsonPath("$.error_message").value("피드백을 찾을 수 없습니다."))
                    .andExpect(jsonPath("$.errors").isEmpty());
        }
    }
}
