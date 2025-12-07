package com.techeer.backend.api.career.adapter.out.persistence;

import com.techeer.backend.api.career.application.port.out.LoadUserCareerPort;
import com.techeer.backend.api.career.application.port.out.SaveUserCareerPort;
import com.techeer.backend.api.career.domain.UserCareer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCareerPersistenceAdapter implements SaveUserCareerPort, LoadUserCareerPort {

    private final UserCareerJpaRepository userCareerJpaRepository;

    @Override
    public UserCareer saveUserCareer(UserCareer userCareer) {
        return userCareerJpaRepository.save(userCareer);
    }

    @Override
    public Optional<UserCareer> findById(Long id) {
        return userCareerJpaRepository.findById(id);
    }
}
