package com.techeer.backend.api.document.application.port.out;

import com.techeer.backend.api.document.domain.Education;
import com.techeer.backend.api.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface LoadEducationPort {
    Optional<Education> findById(Long id);
    Slice<Education> findAllByUser(User user, Pageable pageable);
}

