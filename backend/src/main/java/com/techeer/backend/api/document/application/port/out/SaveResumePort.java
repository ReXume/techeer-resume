package com.techeer.backend.api.document.application.port.out;

import com.techeer.backend.api.document.domain.Resume;

public interface SaveResumePort {

	Resume saveResume(Resume resume);

}
