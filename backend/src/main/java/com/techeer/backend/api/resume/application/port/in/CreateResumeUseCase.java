package com.techeer.backend.api.resume.application.port.in;

import com.techeer.backend.api.resume.dto.request.ResumeCreateRequest;

public interface CreateResumeUseCase {
	Long createResume(ResumeCreateRequest request);
}

