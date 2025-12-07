package com.techeer.backend.api.resume.application.port.out;

import com.techeer.backend.api.resume.domain.Resume;

public interface SaveResumePort {
	Resume saveResume(Resume resume);
}

