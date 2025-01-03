package com.techeer.backend.api.resume.dto.response;

import com.techeer.backend.api.feedback.dto.response.FeedbackResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResumeDetailResponse {

    private Long resumeId;
    private String userName;
    private String resumeName;
    private String position;
    private String fileUrl;
    private int career;
    private List<String> techStackNames;
    private List<String> companyNames;
    private final List<FeedbackResponse> feedbackResponses;

    private final Long previousResumeId;
    private final Long laterResumeId;
}
