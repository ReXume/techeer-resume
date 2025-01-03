package com.techeer.backend.api.feedback.controller;

import com.techeer.backend.api.aifeedback.domain.AIFeedback;
import com.techeer.backend.api.feedback.converter.FeedbackConverter;
import com.techeer.backend.api.feedback.domain.Feedback;
import com.techeer.backend.api.feedback.dto.request.FeedbackCreateRequest;
import com.techeer.backend.api.feedback.dto.response.AllFeedbackResponse;
import com.techeer.backend.api.feedback.dto.response.FeedbackResponse;
import com.techeer.backend.api.feedback.service.FeedbackService;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.common.response.CommonResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "이력서 피드백 등록 API", description = "Feedback API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resumes/")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserService userService;

    @Operation(summary = "피드백 등록", description = "원하는 위치에 피드백을 작성합니다.")
    @PostMapping("/{resume_id}/feedbacks")
    public CommonResponse<FeedbackResponse> createFeedback(
            @PathVariable("resume_id") Long resumeId,
            @Valid @RequestBody FeedbackCreateRequest feedbackRequest) {

        log.info("이력서 ID: {} 에 대한 피드백 생성 요청", resumeId);

        User user = userService.getLoginUser();

        // feedback 엔티티 반환
        Feedback feedback = feedbackService.createFeedback(user, resumeId, feedbackRequest);

        // feedbackresponse로 변환
        FeedbackResponse feedbackResponse = FeedbackConverter.toFeedbackResponse(feedback);

        log.info("피드백 생성 완료: {}", feedbackResponse);

        return CommonResponse.of(SuccessCode.CREATED, feedbackResponse);
    }

    @Operation(summary = "AI 피드백, 일반 피드백 조회", description = "해당 이력서에 대한 AI 피드백과 일반 피드백 조회")
    @GetMapping("/{resume_id}/feedbacks")
    public CommonResponse<AllFeedbackResponse> getFeedbackWithAIFeedback(@PathVariable("resume_id") Long resumeId) {

        // 엔티티, 리스트 반환
        List<Feedback> feedbacks = feedbackService.getFeedbackByResumeId(resumeId);
        AIFeedback aiFeedback = feedbackService.getAIFeedbackByResumeId(resumeId);

        AllFeedbackResponse response = FeedbackConverter.toAllFeedbackResponse(feedbacks, aiFeedback);

        return CommonResponse.of(SuccessCode.FEEDBACK_FETCH_OK, response);
    }

    @Operation(summary = "피드백 삭제")
    @DeleteMapping("/{resume_id}/feedbacks/{feedback_id}")
    public CommonResponse<Void> deleteFeedback(
            @PathVariable("resume_id") Long resumeId,
            @PathVariable("feedback_id") Long feedbackId) {

        User user = userService.getLoginUser();

        log.info("이력서 ID: {}, 피드백 ID: {} 에 대한 삭제 요청", resumeId, feedbackId);

        feedbackService.deleteFeedbackById(user, resumeId, feedbackId);

        return CommonResponse.of(SuccessCode.NO_CONTENT, null);
    }
}
