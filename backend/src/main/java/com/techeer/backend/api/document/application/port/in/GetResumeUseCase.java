package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.response.ResumeInfoResponse;

public interface GetResumeUseCase {

	ResumeInfoResponse getResume(Long resumeId);

}
