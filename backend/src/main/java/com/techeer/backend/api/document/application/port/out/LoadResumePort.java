package com.techeer.backend.api.document.application.port.out;

import com.techeer.backend.api.document.domain.Resume;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface LoadResumePort {
    Optional<Resume> findById(Long id);
    Slice<Resume> findAllByUser(User user, Pageable pageable);
}

