package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.DeleteResumeUseCase;
import com.techeer.backend.api.document.application.port.out.LoadResumePort;
import com.techeer.backend.api.document.domain.Resume;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteResumeService implements DeleteResumeUseCase {

	private final LoadResumePort loadResumePort;

	@Override
	public void deleteResume(Long resumeId, Long userId) {
		Resume resume = loadResumePort.findById(resumeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

		if (!resume.getFile().getUser().getId().equals(userId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		resume.softDelete();
	}

}
