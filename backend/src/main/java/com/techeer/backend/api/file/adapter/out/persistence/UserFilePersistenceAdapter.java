package com.techeer.backend.api.file.adapter.out.persistence;

import com.techeer.backend.api.file.application.port.out.LoadUserFilePort;
import com.techeer.backend.api.file.application.port.out.SaveUserFilePort;
import com.techeer.backend.api.file.domain.UserFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserFilePersistenceAdapter implements LoadUserFilePort, SaveUserFilePort {

	private final UserFileJpaRepository userFileJpaRepository;

	@Override
	public Optional<UserFile> findById(Long id) {
		return userFileJpaRepository.findById(id);
	}

	@Override
	public UserFile saveUserFile(UserFile userFile) {
		return userFileJpaRepository.save(userFile);
	}

}
