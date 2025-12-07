package com.techeer.backend.api.career.application.service;

import com.techeer.backend.api.career.application.port.in.DeleteUserCareerUseCase;
import com.techeer.backend.api.career.application.port.out.LoadUserCareerPort;
import com.techeer.backend.api.career.domain.UserCareer;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteUserCareerService implements DeleteUserCareerUseCase {

    private final LoadUserCareerPort loadUserCareerPort;

    @Override
    public void deleteUserCareer(Long careerId, Long userId) {
        UserCareer userCareer = loadUserCareerPort.findById(careerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_CAREER_NOT_FOUND));

        if (!userCareer.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        userCareer.softDelete();
    }
}

