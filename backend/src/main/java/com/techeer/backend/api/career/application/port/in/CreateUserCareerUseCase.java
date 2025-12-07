package com.techeer.backend.api.career.application.port.in;

import com.techeer.backend.api.career.dto.request.UserCareerCreateRequest;

public interface CreateUserCareerUseCase {
	Long createUserCareer(UserCareerCreateRequest request);
}

