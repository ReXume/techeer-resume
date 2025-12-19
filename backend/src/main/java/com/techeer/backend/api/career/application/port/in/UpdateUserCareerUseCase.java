package com.techeer.backend.api.career.application.port.in;

import com.techeer.backend.api.career.dto.request.UserCareerUpdateRequest;

public interface UpdateUserCareerUseCase {

	void updateUserCareer(Long careerId, UserCareerUpdateRequest request, Long userId);

}
