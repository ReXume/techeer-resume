package com.techeer.backend.api.aifeedback.converter;

import com.techeer.backend.api.aifeedback.domain.AIFeedback;
import com.techeer.backend.api.aifeedback.dto.AIFeedbackResponse;

public class AIFeedbackConverter {
    public static AIFeedbackResponse toResponse(AIFeedback aifeedback) {
        if (aifeedback == null) {
            return null;
        }
        return AIFeedbackResponse.builder()
                .id(aifeedback.getId())
                .resumeId(aifeedback.getResumeId())
                .feedback(aifeedback.getFeedback())
                .build();
    }
}

