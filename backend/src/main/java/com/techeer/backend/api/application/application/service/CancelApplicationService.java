package com.techeer.backend.api.application.application.service;

import com.techeer.backend.api.application.application.port.in.CancelApplicationUseCase;
import com.techeer.backend.api.application.application.port.out.LoadApplicationPort;
import com.techeer.backend.api.application.domain.Application;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelApplicationService implements CancelApplicationUseCase {

    private final LoadApplicationPort loadApplicationPort;

    @Override
    public void cancelApplication(Long applicationId, Long userId) {
        Application application = loadApplicationPort.findById(applicationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        application.softDelete();
    }
}

