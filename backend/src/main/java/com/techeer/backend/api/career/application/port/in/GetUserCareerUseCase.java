package com.techeer.backend.api.career.application.port.in;

import com.techeer.backend.api.career.dto.response.UserCareerInfoResponse;

public interface GetUserCareerUseCase {
    UserCareerInfoResponse getUserCareer(Long careerId);
}

