package com.techeer.backend.api.file.adapter.out.persistence;

import com.techeer.backend.api.file.application.port.out.LoadUserFilePort;
import com.techeer.backend.api.file.domain.UserFile;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserFilePersistenceAdapter implements LoadUserFilePort {

	private final UserFileJpaRepository userFileJpaRepository;

	@Override
	public Optional<UserFile> findById(Long id) {
		return userFileJpaRepository.findById(id);
	}
}

