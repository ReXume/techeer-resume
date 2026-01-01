package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.DeleteEducationUseCase;
import com.techeer.backend.api.document.application.port.out.LoadEducationPort;
import com.techeer.backend.api.document.domain.Education;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteEducationService implements DeleteEducationUseCase {

    private final LoadEducationPort loadEducationPort;

    @Override
    public void deleteEducation(Long educationId, Long userId) {
        Education education = loadEducationPort.findById(educationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EDUCATION_NOT_FOUND));

        if (!education.getFile().getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        education.softDelete();
    }

}
