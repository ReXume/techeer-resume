package com.techeer.backend.api.resume.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.techeer.backend.api.aifeedback.domain.AIFeedback;
import com.techeer.backend.api.aifeedback.repository.AIFeedbackRepository;
import com.techeer.backend.api.feedback.domain.Feedback;
import com.techeer.backend.api.feedback.dto.request.FeedbackCreateRequest;
import com.techeer.backend.api.feedback.repository.FeedbackRepository;
import com.techeer.backend.api.feedback.service.FeedbackService;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {


    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private AIFeedbackRepository aiFeedbackRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        feedbackRepository = Mockito.mock(FeedbackRepository.class);
        resumeRepository = Mockito.mock(ResumeRepository.class);
        aiFeedbackRepository = Mockito.mock(AIFeedbackRepository.class);

        feedbackService = new FeedbackService(feedbackRepository, resumeRepository, aiFeedbackRepository);
    }

    @Nested
    @DisplayName("createFeedback 메서드 테스트")
    class CreateFeedbackTest {

        @Test
        @DisplayName("Given 유효한 이력서와 유저, When 피드백 생성 요청, Then 피드백이 성공적으로 생성된다.")
        void createFeedback_Success() {
            // Given
            Long resumeId = 1L;
            User user = userRepository.save(
                    User.builder()
                            .email("john@example.com")
                            .username("JohnDoe")
                            .role(Role.TECHEER)
                            .socialType(SocialType.GOOGLE)
                            .build()
            );

            FeedbackCreateRequest request = new FeedbackCreateRequest(
                    "Valid content",
                    100.5, 200.5,
                    null, null,
                    1
            );
            Resume mockResume = Resume.builder().id(resumeId).build();

            when(resumeRepository.findByIdAndDeletedAtIsNull(resumeId)).thenReturn(Optional.of(mockResume));
            when(feedbackRepository.save(Mockito.any(Feedback.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Feedback createdFeedback = feedbackService.createFeedback(user, resumeId, request);

            // Then
            assertThat(createdFeedback).isNotNull();
            assertThat(createdFeedback.getContent()).isEqualTo("Valid content");
            assertThat(createdFeedback.getResume()).isEqualTo(mockResume);
        }

        @Test
        @DisplayName("Given 존재하지 않는 이력서, When 피드백 생성 요청, Then RESUME_NOT_FOUND 예외가 발생한다.")
        void createFeedback_ResumeNotFound() {
            // Given
            Long resumeId = 999L;
            User user = userRepository.save(
                    User.builder()
                            .email("john@example.com")
                            .username("JohnDoe")
                            .role(Role.TECHEER)
                            .socialType(SocialType.GOOGLE)
                            .build()
            );
            FeedbackCreateRequest request = new FeedbackCreateRequest(
                    "Content",
                    100.5, 200.5,
                    null, null,
                    1
            );

            when(resumeRepository.findByIdAndDeletedAtIsNull(resumeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> feedbackService.createFeedback(user, resumeId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.RESUME_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("deleteFeedbackById 메서드 테스트")
    class DeleteFeedbackTest {

        @Test
        @DisplayName("Given 존재하지 않는 이력서, When 피드백 삭제 요청, Then RESUME_NOT_FOUND 예외 발생")
        void deleteFeedback_ResumeNotFound() {
            // Given
            Long resumeId = 999L;
            Long feedbackId = 10L;
            User user = userRepository.save(
                    User.builder()
                            .email("john@example.com")
                            .username("JohnDoe")
                            .role(Role.TECHEER)
                            .socialType(SocialType.GOOGLE)
                            .build()
            );

            when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> feedbackService.deleteFeedbackById(user, resumeId, feedbackId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.RESUME_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("Given 존재하지 않는 피드백, When 피드백 삭제 요청, Then FEEDBACK_NOT_FOUND 예외 발생")
        void deleteFeedback_FeedbackNotFound() {
            // Given
            Long resumeId = 1L;
            Long feedbackId = 999L;
            User user = userRepository.save(
                    User.builder()
                            .email("john@example.com")
                            .username("JohnDoe")
                            .role(Role.TECHEER)
                            .socialType(SocialType.GOOGLE)
                            .build()
            );
            Resume resume = Resume.builder().id(resumeId).build();

            when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
            when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> feedbackService.deleteFeedbackById(user, resumeId, feedbackId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.FEEDBACK_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("getFeedbackByResumeId 메서드 테스트")
    class GetFeedbackByResumeIdTest {

        @Test
        @DisplayName("Given 존재하는 이력서와 피드백 목록, When 피드백 조회, Then 피드백 리스트 반환")
        void getFeedbackByResumeId_Success() {
            // Given
            Long resumeId = 1L;
            Resume resume = Resume.builder().id(resumeId).build();
            Feedback f1 = Feedback.builder().id(10L).resume(resume).build();
            Feedback f2 = Feedback.builder().id(11L).resume(resume).build();

            when(resumeRepository.existsById(resumeId)).thenReturn(true);
            when(feedbackRepository.findAllByResumeId(resumeId)).thenReturn(List.of(f1, f2));

            // When
            List<Feedback> feedbacks = feedbackService.getFeedbackByResumeId(resumeId);

            // Then
            assertThat(feedbacks).hasSize(2);
        }

        @Test
        @DisplayName("Given 존재하지 않는 이력서, When 피드백 조회, Then RESUME_NOT_FOUND 예외 발생")
        void getFeedbackByResumeId_ResumeNotFound() {
            // Given
            Long resumeId = 999L;
            when(resumeRepository.existsById(resumeId)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> feedbackService.getFeedbackByResumeId(resumeId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.RESUME_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("getAIFeedbackByResumeId 메서드 테스트")
    class GetAIFeedbackByResumeIdTest {

        @Test
        @DisplayName("Given 존재하는 AI 피드백, When 조회, Then 해당 AI 피드백 반환")
        void getAIFeedbackByResumeId_Found() {
            // Given
            Long resumeId = 1L;
            AIFeedback aiFeedback = AIFeedback.builder().resumeId(resumeId).feedback("Test AI").build();
            when(aiFeedbackRepository.findByResumeId(resumeId)).thenReturn(Optional.of(aiFeedback));

            // When
            AIFeedback result = feedbackService.getAIFeedbackByResumeId(resumeId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getFeedback()).isEqualTo("Test AI");
        }

        @Test
        @DisplayName("Given AI 피드백 없음, When 조회, Then empty AI 피드백 반환")
        void getAIFeedbackByResumeId_Empty() {
            // Given
            Long resumeId = 1L;
            when(aiFeedbackRepository.findByResumeId(resumeId)).thenReturn(Optional.empty());

            // When
            AIFeedback result = feedbackService.getAIFeedbackByResumeId(resumeId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getFeedback()).isEqualTo("No AI Feedback available");
        }
    }

    @Nested
    @DisplayName("getFeedbacksByResumeId 메서드 테스트")
    class GetFeedbacksByResumeIdTest {

        @Test
        @DisplayName("Given 피드백 리스트, When 조회, Then 해당 리스트 반환")
        void getFeedbacksByResumeId_Success() {
            Long resumeId = 1L;
            Feedback f1 = Feedback.builder().id(10L).build();
            Feedback f2 = Feedback.builder().id(11L).build();

            when(feedbackRepository.findAllByResumeId(resumeId)).thenReturn(List.of(f1, f2));

            List<Feedback> results = feedbackService.getFeedbacksByResumeId(resumeId);
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("Given 피드백 없음, When 조회, Then 빈 리스트 반환")
        void getFeedbacksByResumeId_Empty() {
            Long resumeId = 1L;
            when(feedbackRepository.findAllByResumeId(resumeId)).thenReturn(List.of());

            List<Feedback> results = feedbackService.getFeedbacksByResumeId(resumeId);
            assertThat(results).isEmpty();
        }
    }
}
