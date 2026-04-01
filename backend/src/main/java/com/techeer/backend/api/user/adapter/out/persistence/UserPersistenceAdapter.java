package com.techeer.backend.api.user.adapter.out.persistence;

import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.application.port.out.SaveUserPort;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort {

	private final UserRepository userRepository;

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public Optional<User> findByRefreshToken(String refreshToken) {
		return userRepository.findByRefreshToken(refreshToken);
	}

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}

}
