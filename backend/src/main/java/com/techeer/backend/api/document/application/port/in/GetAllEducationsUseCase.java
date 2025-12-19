package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.response.EducationInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GetAllEducationsUseCase {
    Slice<EducationInfoResponse> getAllEducations(Long userId, Pageable pageable);
}

