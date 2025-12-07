package com.techeer.backend.api.resume.application.port.in;

import com.techeer.backend.api.resume.dto.response.ResumeInfoResponse;

public interface GetResumeUseCase {
    ResumeInfoResponse getResume(Long resumeId);
}

