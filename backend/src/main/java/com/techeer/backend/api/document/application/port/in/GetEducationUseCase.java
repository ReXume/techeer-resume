package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.response.EducationInfoResponse;

public interface GetEducationUseCase {
    EducationInfoResponse getEducation(Long educationId);
}

