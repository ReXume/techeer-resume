package com.techeer.backend.api.document.application.port.out;

import com.techeer.backend.api.document.domain.Education;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface LoadEducationPort {

	Optional<Education> findById(Long id);

	Slice<Education> findAllByUser(User user, Pageable pageable);

}
