package com.techeer.backend.api.career.adapter.out.persistence;

import com.techeer.backend.api.career.application.port.out.SaveUserCareerPort;
import com.techeer.backend.api.career.domain.UserCareer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCareerPersistenceAdapter implements SaveUserCareerPort {

	private final UserCareerJpaRepository userCareerJpaRepository;

	@Override
	public UserCareer saveUserCareer(UserCareer userCareer) {
		return userCareerJpaRepository.save(userCareer);
	}
}

