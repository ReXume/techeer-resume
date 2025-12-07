package com.techeer.backend.api.resume.application.port.in;

import com.techeer.backend.api.resume.dto.request.ResumeUpdateRequest;

public interface UpdateResumeUseCase {
    void updateResume(Long resumeId, ResumeUpdateRequest request);
}

