package com.techeer.backend.api.feedback.dto.response;

import com.techeer.backend.api.user.dto.response.UserInfoResponse;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class FeedbackResponse {
    private final UserInfoResponse userInfoResponse;
    private final Long feedbackId;
    private final Long resumeId;
    private final String content;
    private final Double x1;
    private final Double y1;
    private final Double x2;
    private final Double y2;

    private final int pageNumber;
    private final LocalDateTime createdAt;
}
