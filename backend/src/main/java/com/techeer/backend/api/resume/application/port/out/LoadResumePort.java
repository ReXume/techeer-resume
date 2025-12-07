package com.techeer.backend.api.resume.application.port.out;

import com.techeer.backend.api.resume.domain.Resume;
import java.util.Optional;

public interface LoadResumePort {
    Optional<Resume> findById(Long id);
}

