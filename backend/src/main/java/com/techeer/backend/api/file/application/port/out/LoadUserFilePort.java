package com.techeer.backend.api.file.application.port.out;

import com.techeer.backend.api.file.domain.UserFile;
import java.util.Optional;

public interface LoadUserFilePort {
	Optional<UserFile> findById(Long id);
}

