package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.GetAllEducationsUseCase;
import com.techeer.backend.api.document.application.port.out.LoadEducationPort;
import com.techeer.backend.api.document.dto.response.EducationInfoResponse;
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
public class GetAllEducationsService implements GetAllEducationsUseCase {

    private final LoadEducationPort loadEducationPort;

    private final LoadUserPort loadUserPort;

    @Override
    public Slice<EducationInfoResponse> getAllEducations(Long userId, Pageable pageable) {
        User user = loadUserPort.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return loadEducationPort.findAllByUser(user, pageable)
                .map(education -> EducationInfoResponse.builder()
                        .id(education.getId())
                        .title(education.getTitle())
                        .fileUrl(education.getFile().getFileUrl())
                        .isDefault(education.getIsDefault())
                        .build());
    }

}
