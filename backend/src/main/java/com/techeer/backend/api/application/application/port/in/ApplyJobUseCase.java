package com.techeer.backend.api.application.application.port.in;

import com.techeer.backend.api.application.dto.request.ApplicationApplyRequest;

public interface ApplyJobUseCase {

	Long applyJob(ApplicationApplyRequest request, Long userId);

}
