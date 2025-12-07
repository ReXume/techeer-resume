package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.request.ResumeUpdateRequest;

public interface UpdateResumeUseCase {
    void updateResume(Long resumeId, ResumeUpdateRequest request);
}

