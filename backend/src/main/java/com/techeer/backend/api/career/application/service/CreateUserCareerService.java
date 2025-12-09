package com.techeer.backend.api.career.application.service;

import com.techeer.backend.api.career.application.port.in.CreateUserCareerUseCase;
import com.techeer.backend.api.career.application.port.out.SaveUserCareerPort;
import com.techeer.backend.api.career.domain.UserCareer;
import com.techeer.backend.api.career.dto.request.UserCareerCreateRequest;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserCareerService implements CreateUserCareerUseCase {

	private final SaveUserCareerPort saveUserCareerPort;
	private final LoadUserPort loadUserPort;

	@Override
	public Long createUserCareer(UserCareerCreateRequest request, Long userId) {
		User user = loadUserPort.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		UserCareer userCareer = UserCareer.builder()
			.user(user)
			.companyName(request.companyName())
			.jobTitle(request.jobTitle())
			.isCurrent(request.isCurrent())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.build();

		return saveUserCareerPort.saveUserCareer(userCareer).getId();
	}
}

