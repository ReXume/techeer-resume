package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.request.EducationUpdateRequest;

public interface UpdateEducationUseCase {
    void updateEducation(Long educationId, EducationUpdateRequest request, Long userId);
}

