package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.response.ResumeInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GetAllResumesUseCase {

    Slice<ResumeInfoResponse> getAllResumes(Long userId, Pageable pageable);

}
