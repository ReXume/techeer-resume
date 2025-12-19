package com.techeer.backend.api.document.application.port.in;

public interface DeleteResumeUseCase {

	void deleteResume(Long resumeId, Long userId);

}
