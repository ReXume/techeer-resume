package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.UpdateEducationUseCase;
import com.techeer.backend.api.document.application.port.out.LoadEducationPort;
import com.techeer.backend.api.document.domain.Education;
import com.techeer.backend.api.document.dto.request.EducationUpdateRequest;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateEducationService implements UpdateEducationUseCase {

    private final LoadEducationPort loadEducationPort;

    @Override
    public void updateEducation(Long educationId, EducationUpdateRequest request, Long userId) {
        Education education = loadEducationPort.findById(educationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EDUCATION_NOT_FOUND));

        if (!education.getFile().getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        education.updateTitle(request.title());

        if (request.isDefault() != null) {
            if (request.isDefault()) {
                education.setAsDefault();
            } else {
                education.unsetAsDefault();
            }
        }
    }

}
