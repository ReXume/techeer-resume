package com.techeer.backend.api.feedback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class FeedbackResponse {
    private final Long feedbackId;
    private final Long resumeId;
    private final String content;
    private final Double xCoordinate;
    private final Double yCoordinate;
    private final int pageNumber;
}
