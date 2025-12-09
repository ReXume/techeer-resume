package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.request.ResumeCreateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface CreateResumeUseCase {
	Long createResume(ResumeCreateRequest request, MultipartFile file, Long userId);
}
