package com.techeer.backend.api.feedback.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class AllFeedbackResponse {
    private final List<FeedbackResponse> feedbackResponses; // 여러 개의 피드백을 리스트로 포함
    private final String aiFeedbackContent;
    private final Long aiFeedbackId;
}
