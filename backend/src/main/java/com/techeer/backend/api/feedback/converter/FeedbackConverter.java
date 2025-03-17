package com.techeer.backend.api.feedback.converter;

import com.techeer.backend.api.aifeedback.domain.AIFeedback;
import com.techeer.backend.api.feedback.domain.Feedback;
import com.techeer.backend.api.feedback.dto.request.FeedbackCreateRequest;
import com.techeer.backend.api.feedback.dto.response.AllFeedbackResponse;
import com.techeer.backend.api.feedback.dto.response.FeedbackResponse;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.user.domain.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FeedbackConverter {

    // 엔티티 -> 응답 DTO 변환
    public static FeedbackResponse toFeedbackResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .feedbackId(feedback.getId())
                .resumeId(feedback.getResume().getId())
                .content(feedback.getContent())
                .x1(feedback.getX1())  // 추가
                .y1(feedback.getY1())  // 추가
                .x2(feedback.getX2())  // 추가
                .y2(feedback.getY2())  // 추가
                .pageNumber(feedback.getPageNumber())
                .build();
    }

    // 엔티티 리스트 -> 응답 DTO 리스트 변환
    public static List<FeedbackResponse> toFeedbackResponses(List<Feedback> feedbacks) {
        return feedbacks.stream()
                .map(FeedbackConverter::toFeedbackResponse)
                .collect(Collectors.toList());
    }

    // 엔티티 리스트와 AI 피드백 -> AllFeedbackResponse 변환
    public static AllFeedbackResponse toAllFeedbackResponse(List<Feedback> feedbacks, AIFeedback aiFeedback) {
        return AllFeedbackResponse.builder()
                .feedbackResponses(toFeedbackResponses(feedbacks))
                .aiFeedbackContent(getAIFeedbackContent(aiFeedback))
                .build();
    }

    // 요청 DTO -> 엔티티 변환
    public static Feedback toFeedbackEntity(User user, Resume resume, FeedbackCreateRequest dto) {
        return Feedback.builder()
                .user(user)
                .resume(resume)
                .content(dto.getContent())
                .x1(dto.getX1())  // 추가
                .y1(dto.getY1())  // 추가
                .x2(dto.getX2())  // 추가
                .y2(dto.getY2())  // 추가
                .pageNumber(dto.getPageNumber())
                .build();
    }

    // 헬퍼 메서드: AI 피드백 내용 추출
    private static String getAIFeedbackContent(AIFeedback aiFeedback) {
        return Optional.ofNullable(aiFeedback)
                .map(AIFeedback::getFeedback)
                .orElse(null);
    }
}