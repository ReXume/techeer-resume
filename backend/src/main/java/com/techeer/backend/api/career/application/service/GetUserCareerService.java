package com.techeer.backend.api.career.application.service;

import com.techeer.backend.api.career.application.port.in.GetUserCareerUseCase;
import com.techeer.backend.api.career.application.port.out.LoadUserCareerPort;
import com.techeer.backend.api.career.domain.UserCareer;
import com.techeer.backend.api.career.dto.response.UserCareerInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserCareerService implements GetUserCareerUseCase {

    private final LoadUserCareerPort loadUserCareerPort;

    @Override
    public UserCareerInfoResponse getUserCareer(Long careerId) {
        UserCareer userCareer = loadUserCareerPort.findById(careerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_CAREER_NOT_FOUND));

        return UserCareerInfoResponse.builder()
            .id(userCareer.getId())
            .userId(userCareer.getUser().getId())
            .companyName(userCareer.getCompanyName())
            .jobTitle(userCareer.getJobTitle())
            .isCurrent(userCareer.getIsCurrent())
            .startDate(userCareer.getStartDate())
            .endDate(userCareer.getEndDate())
            .build();
    }
}

