package com.techeer.backend.api.resume.application.port.in;

public interface DeleteResumeUseCase {
    void deleteResume(Long resumeId, Long userId);
}

