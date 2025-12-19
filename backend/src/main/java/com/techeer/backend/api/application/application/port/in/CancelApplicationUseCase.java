package com.techeer.backend.api.application.application.port.in;

public interface CancelApplicationUseCase {

	void cancelApplication(Long applicationId, Long userId);

}
