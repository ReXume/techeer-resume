package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.GetAllResumesUseCase;
import com.techeer.backend.api.document.application.port.out.LoadResumePort;
import com.techeer.backend.api.document.dto.response.ResumeInfoResponse;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllResumesService implements GetAllResumesUseCase {

    private final LoadResumePort loadResumePort;

    private final LoadUserPort loadUserPort;

    @Override
    public Slice<ResumeInfoResponse> getAllResumes(Long userId, Pageable pageable) {
        User user = loadUserPort.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return loadResumePort.findAllByUser(user, pageable)
                .map(resume -> ResumeInfoResponse.builder()
                        .id(resume.getId())
                        .title(resume.getTitle())
                        .fileUrl(resume.getFile().getFileUrl())
                        .isDefault(resume.getIsDefault())
                        .build());
    }

}
