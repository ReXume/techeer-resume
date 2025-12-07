package com.techeer.backend.api.resume.service;

import com.techeer.backend.api.file.domain.UserFile;
import com.techeer.backend.api.file.repository.UserFileRepository;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.dto.request.ResumeCreateRequest;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {

	private final ResumeRepository resumeRepository;
	private final UserFileRepository userFileRepository;

	@Transactional
	public Long createResume(ResumeCreateRequest request) {
		UserFile file = userFileRepository.findById(request.fileId())
			.orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

		Resume resume = Resume.builder()
			.file(file)
			.title(request.title())
			.isDefault(request.isDefault())
			.build();

		return resumeRepository.save(resume).getId();
	}

	public Resume getResume(Long id) {
		return resumeRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));
	}
}

