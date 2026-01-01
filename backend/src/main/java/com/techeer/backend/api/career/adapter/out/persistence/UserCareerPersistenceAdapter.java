package com.techeer.backend.api.career.adapter.out.persistence;

import com.techeer.backend.api.career.application.port.out.LoadUserCareerPort;
import com.techeer.backend.api.career.application.port.out.SaveUserCareerPort;
import com.techeer.backend.api.career.domain.UserCareer;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
        // Soft Delete 적용: 삭제되지 않은 사용자 경력만 조회
        return userCareerJpaRepository.findByIdAndNotDeleted(id);
    }

}
