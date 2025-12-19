package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.request.EducationCreateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface CreateEducationUseCase {
    Long createEducation(EducationCreateRequest request, MultipartFile file, Long userId);
}

