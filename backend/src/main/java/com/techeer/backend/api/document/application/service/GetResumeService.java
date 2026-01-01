package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.GetResumeUseCase;
import com.techeer.backend.api.document.application.port.out.LoadResumePort;
import com.techeer.backend.api.document.domain.Resume;
import com.techeer.backend.api.document.dto.response.ResumeInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetResumeService implements GetResumeUseCase {

    private final LoadResumePort loadResumePort;

    @Override
    public ResumeInfoResponse getResume(Long resumeId) {
        Resume resume = loadResumePort.findById(resumeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        return ResumeInfoResponse.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .fileUrl(resume.getFile().getFileUrl())
                .isDefault(resume.getIsDefault())
                .build();
    }

}
