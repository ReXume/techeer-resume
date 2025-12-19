package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.GetEducationUseCase;
import com.techeer.backend.api.document.application.port.out.LoadEducationPort;
import com.techeer.backend.api.document.domain.Education;
import com.techeer.backend.api.document.dto.response.EducationInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEducationService implements GetEducationUseCase {

    private final LoadEducationPort loadEducationPort;

    @Override
    public EducationInfoResponse getEducation(Long educationId) {
        Education education = loadEducationPort.findById(educationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.EDUCATION_NOT_FOUND));

        return EducationInfoResponse.builder()
            .id(education.getId())
            .title(education.getTitle())
            .fileUrl(education.getFile().getFileUrl())
            .isDefault(education.getIsDefault())
            .build();
    }
}

