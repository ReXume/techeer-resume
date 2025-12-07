package com.techeer.backend.api.resume.application.service;

import com.techeer.backend.api.resume.application.port.in.UpdateResumeUseCase;
import com.techeer.backend.api.resume.application.port.out.LoadResumePort;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.dto.request.ResumeUpdateRequest;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateResumeService implements UpdateResumeUseCase {

    private final LoadResumePort loadResumePort;

    @Override
    public void updateResume(Long resumeId, ResumeUpdateRequest request) {
        Resume resume = loadResumePort.findById(resumeId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        if (!resume.getFile().getUser().getId().equals(request.userId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        resume.updateTitle(request.title());

        if (request.isDefault() != null) {
            if (request.isDefault()) {
                resume.setAsDefault();
            } else {
                resume.unsetAsDefault();
            }
        }
    }
}

