package com.techeer.backend.api.resume.adapter.out.persistence;

import com.techeer.backend.api.resume.application.port.out.SaveResumePort;
import com.techeer.backend.api.resume.domain.Resume;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ResumePersistenceAdapter implements SaveResumePort {

	private final ResumeJpaRepository resumeJpaRepository;

	@Override
	public Resume saveResume(Resume resume) {
		return resumeJpaRepository.save(resume);
	}
}

