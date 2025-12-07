package com.techeer.backend.api.application.adapter.out.persistence;

import com.techeer.backend.api.application.application.port.out.LoadApplicationPort;
import com.techeer.backend.api.application.application.port.out.SaveApplicationPort;
import com.techeer.backend.api.application.domain.Application;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplicationPersistenceAdapter implements SaveApplicationPort, LoadApplicationPort {

    private final ApplicationJpaRepository applicationJpaRepository;

    @Override
    public Application saveApplication(Application application) {
        return applicationJpaRepository.save(application);
    }

    @Override
    public boolean existsByUserAndJobPosting(User user, JobPosting jobPosting) {
        return applicationJpaRepository.existsByUserAndJobPosting(user, jobPosting);
    }

    @Override
    public Optional<Application> findById(Long id) {
        return applicationJpaRepository.findById(id);
    }
}
