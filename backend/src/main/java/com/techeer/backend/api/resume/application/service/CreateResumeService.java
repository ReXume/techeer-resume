package com.techeer.backend.api.resume.application.service;

import com.techeer.backend.api.file.application.port.out.LoadUserFilePort;
import com.techeer.backend.api.file.domain.UserFile;
import com.techeer.backend.api.resume.application.port.in.CreateResumeUseCase;
import com.techeer.backend.api.resume.application.port.out.SaveResumePort;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.dto.request.ResumeCreateRequest;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateResumeService implements CreateResumeUseCase {

	private final SaveResumePort saveResumePort;

	private final LoadUserFilePort loadUserFilePort;

	@Override
	public Long createResume(ResumeCreateRequest request) {
		UserFile file = loadUserFilePort.findById(request.fileId())
			.orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

		Resume resume = Resume.builder()
			.file(file)
			.title(request.title())
			.isDefault(request.isDefault())
			.build();

		return saveResumePort.saveResume(resume).getId();
	}
}

