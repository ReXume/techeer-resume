package com.techeer.backend.api.application.application.port.in;

import com.techeer.backend.api.application.dto.response.ApplicationInfoResponse;

public interface GetApplicationUseCase {

	ApplicationInfoResponse getApplication(Long applicationId);

}
