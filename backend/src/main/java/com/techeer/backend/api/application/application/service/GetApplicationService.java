package com.techeer.backend.api.application.application.service;

import com.techeer.backend.api.application.application.port.in.GetApplicationUseCase;
import com.techeer.backend.api.application.application.port.out.LoadApplicationPort;
import com.techeer.backend.api.application.domain.Application;
import com.techeer.backend.api.application.dto.response.ApplicationInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetApplicationService implements GetApplicationUseCase {

    private final LoadApplicationPort loadApplicationPort;

    @Override
    public ApplicationInfoResponse getApplication(Long applicationId) {
        Application application = loadApplicationPort.findById(applicationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        return ApplicationInfoResponse.builder()
            .id(application.getId())
            .jobPostingId(application.getJobPosting().getId())
            .jobPostingTitle(application.getJobPosting().getTitle())
            .companyName(application.getJobPosting().getCompany().getName())
            .status(application.getStatus().name())
            .appliedAt(application.getCreatedAt())
            .build();
    }
}

