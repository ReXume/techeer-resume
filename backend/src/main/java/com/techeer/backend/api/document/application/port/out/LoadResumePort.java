package com.techeer.backend.api.document.application.port.out;

import com.techeer.backend.api.document.domain.Resume;
import java.util.Optional;

public interface LoadResumePort {
    Optional<Resume> findById(Long id);
}

